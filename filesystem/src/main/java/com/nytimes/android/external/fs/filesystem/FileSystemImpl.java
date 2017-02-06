package com.nytimes.android.external.fs.filesystem;

import com.nytimes.android.external.cache.CacheLoader;
import com.nytimes.android.external.cache.LoadingCache;
import com.nytimes.android.external.fs.Util;
import com.nytimes.android.external.store.base.RecordState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import okio.BufferedSource;

import static com.nytimes.android.external.cache.CacheBuilder.newBuilder;
import static java.lang.String.format;

/**
 * implements a {@link FileSystem} as regular files on disk in a specific document root (kind of like a root jail)
 * <p>
 * All operations are on the caller's thread.
 */
class FileSystemImpl implements FileSystem {

    private final Util util = new Util();
    @Nonnull
    private final LoadingCache<String, FSFile> files;
    @Nonnull
    private final File root;

    FileSystemImpl(@Nonnull final File root) throws IOException {
        this.root = root;

        this.files = newBuilder().maximumSize(20)
                .build(new CacheLoader<String, FSFile>() {
                    @Nonnull
                    @Override
                    public FSFile load(@Nonnull String path) throws IOException {
                        return new FSFile(root, path);
                    }
                });

        util.createParentDirs(root);
    }

    @Nonnull
    @Override
    public BufferedSource read(@Nonnull String path) throws FileNotFoundException {
        return getFile(path).source();
    }

    @Override
    public void write(@Nonnull String path, BufferedSource source) throws IOException {
        getFile(path).write(source);
    }

    @Override
    public void delete(@Nonnull String path) throws IOException {
        getFile(path).delete();
    }

    @Nonnull
    @Override
    public Collection<String> list(@Nonnull String directory) throws FileNotFoundException {

        Collection<FSFile> foundFiles = findFiles(directory);
        Collection<String> names = new ArrayList<>(foundFiles.size());
        Iterator<FSFile> iterator = foundFiles.iterator();
        while (iterator.hasNext()) {
            names.add(iterator.next().path());
        }
        return names;
    }

    @Override
    public void deleteAll(@Nonnull String directory) throws FileNotFoundException {

        Collection<FSFile> foundFiles = findFiles(directory);
        Iterator<FSFile> iterator = foundFiles.iterator();
        while (iterator.hasNext()) {
            iterator.next().delete();
        }
    }

    @Override
    public boolean exists(@Nonnull String path) {
        return getFile(path).exists();
    }

    @Override
    public RecordState getRecordState(@Nonnull TimeUnit expirationUnit, long expirationDuration, @Nonnull String path) {
        FSFile file = getFile(path);
        if (!file.exists()) {
            return RecordState.MISSING;
        }
        long now = System.currentTimeMillis();
        long cuttOffPoint = now - TimeUnit.MILLISECONDS.convert(expirationDuration, expirationUnit);
        if (file.lastModified() < cuttOffPoint) {
            return RecordState.STALE;
        } else {
            return RecordState.FRESH;
        }
    }

    @Nullable
    private FSFile getFile(@Nonnull String path) {
        return files.getUnchecked(cleanPath(path));
    }

    @Nonnull
    private String cleanPath(@Nonnull String dirty) {
        return util.simplifyPath(dirty);
    }

    @Nonnull
    private Collection<FSFile> findFiles(@Nonnull String path) throws FileNotFoundException {

        File searchRoot = new File(root, util.simplifyPath(path));
        if (searchRoot.exists() && searchRoot.isFile()) {
            throw new FileNotFoundException(format("expecting a directory at %s, instead found a file", path));
        }

        Collection<FSFile> foundFiles = new ArrayList<>();
        BreadthFirstFileTreeIterator iterator = new BreadthFirstFileTreeIterator(searchRoot);
        while (iterator.hasNext()) {
            File file = (File) iterator.next();
            foundFiles.add(files.getUnchecked(util.simplifyPath(file.getPath()
                    .replaceFirst(root.getPath(), ""))));
        }
        return foundFiles;
    }
}
