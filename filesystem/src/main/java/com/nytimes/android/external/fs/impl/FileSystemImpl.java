package com.nytimes.android.external.fs.impl;

import com.nytimes.android.external.cache.CacheLoader;
import com.nytimes.android.external.cache.LoadingCache;
import com.nytimes.android.external.fs.FileSystem;
import com.nytimes.android.external.fs.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import okio.BufferedSource;

import static com.nytimes.android.external.cache.CacheBuilder.newBuilder;
import static java.lang.String.format;

/**
 * implements a {@link FileSystem} as regular files on disk in a specific document root (kind of like a root jail)
 * <p>
 * All operations are on the caller's thread.
 */
public class FileSystemImpl implements FileSystem {

    private final Util util = new Util();
    final LoadingCache<String, FSFile> files;
    final File root;

    public FileSystemImpl(final File root) throws IOException {
        this.root = root;

        this.files = newBuilder().maximumSize(20)
                .build(new CacheLoader<String, FSFile>() {
                    @Override
                    public FSFile load(String path) throws IOException {
                        return new FSFile(root, path);
                    }
                });

        util.createParentDirs(root);
    }

    @Override
    public BufferedSource read(String path) throws FileNotFoundException {
        return getFile(path).source();
    }

    @Override
    public void write(String path, BufferedSource source) throws IOException {
        getFile(path).write(source);
    }

    @Override
    public void delete(String path) throws IOException {
        getFile(path).delete();
    }

    @Override
    public Collection<String> list(String directory) throws FileNotFoundException {

        Collection<FSFile> foundFiles = findFiles(directory);
        Collection<String> names = new ArrayList<>(foundFiles.size());
        Iterator<FSFile> iterator = foundFiles.iterator();
        while ((iterator.hasNext())) {
            names.add(iterator.next().path());
        }
        return names;
    }

    @Override
    public void deleteAll(String directory) throws FileNotFoundException {

        Collection<FSFile> foundFiles = findFiles(directory);
        Iterator<FSFile> iterator = foundFiles.iterator();
        while ((iterator.hasNext())) {
            iterator.next().delete();
        }
    }

    @Override
    public boolean exists(String path) {
        return getFile(path).exists();
    }

    private FSFile getFile(String path) {
        return files.getUnchecked(cleanPath(path));
    }

    private String cleanPath(String dirty) {
        return util.simplifyPath(dirty);
    }

    private Collection<FSFile> findFiles(String path) throws FileNotFoundException {

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
