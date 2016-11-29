package com.nytimes.android.external.store.middleware.fs.impl;


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.nytimes.android.external.store.middleware.fs.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import okio.BufferedSource;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.simplifyPath;
import static java.lang.String.format;


/**
 * implements a {@link FileSystem} as regular files on disk in a specific document root (kind of like a root jail)
 * <p>
 * All operations are on the caller's thread.
 */
public class FileSystemImpl implements FileSystem {

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

        createParentDirs(root);
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
        return findFiles(directory).transform(new Function<FSFile, String>() {
            @Override
            public String apply(FSFile fsFile) {
                return fsFile.path();
            }
        }).toList();
    }

    @Override
    public void deleteAll(String directory) throws FileNotFoundException {
        for (FSFile file : findFiles(directory).toList()) {
            file.delete();
        }
    }

    @Override
    public boolean exists(String path) {
        return getFile(path).exists();
    }

    private FSFile getFile(String path) {
        return files.getUnchecked(cleanPath(path));
    }

    private static String cleanPath(String dirty) {
        return simplifyPath(dirty);
    }

    private FluentIterable<FSFile> findFiles(String path) throws FileNotFoundException {

        File searchRoot = new File(root, simplifyPath(path));
        if (searchRoot.exists() && searchRoot.isFile()) {
            throw new FileNotFoundException(format("expecting a directory at %s, instead found a file", path));
        }
        return Files.fileTreeTraverser()
                .breadthFirstTraversal(searchRoot)
                .filter(new Predicate<File>() {
                    @Override
                    public boolean apply(File file1) {
                        return file1.isFile();
                    }
                })
                .transform(new Function<File, FSFile>() {
                    @Override
                    public FSFile apply(File file) {
                        return files.getUnchecked(simplifyPath(file.getPath()
                                .replaceFirst(root.getPath(), "")));
                    }
                });
    }



}
