package com.nytimes.android.external.fs.impl;

import com.nytimes.android.external.cache.CacheLoader;
import com.nytimes.android.external.cache.LoadingCache;
import com.nytimes.android.external.fs.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import okio.BufferedSource;

import static com.nytimes.android.external.cache.CacheBuilder.newBuilder;
import static dagger.internal.Preconditions.checkNotNull;
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
        return simplifyPath(dirty);
    }

    private Collection<FSFile> findFiles(String path) throws FileNotFoundException {

        File searchRoot = new File(root, simplifyPath(path));
        if (searchRoot.exists() && searchRoot.isFile()) {
            throw new FileNotFoundException(format("expecting a directory at %s, instead found a file", path));
        }

        Collection<FSFile> foundFiles = new ArrayList<>();
        BreadthFirstFileTreeIterator iterator = new BreadthFirstFileTreeIterator(searchRoot);
        while (iterator.hasNext()) {
            File file = (File) iterator.next();
            foundFiles.add(files.getUnchecked(simplifyPath(file.getPath()
                    .replaceFirst(root.getPath(), ""))));
        }
        return foundFiles;
    }

    public String simplifyPath(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }

        String delim = "[/]+";
        String[] arr = path.split(delim);

        Stack<String> stack = new Stack<String>();

        for (String str : arr) {
            if(str.equals("/")){
                continue;
            }
            if (str.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (!str.equals(".") && !str.isEmpty()) {
                stack.push(str);
            }
        }

        StringBuilder sb = new StringBuilder();
        if (stack.isEmpty()) {
            return "/";
        }

        for (String str : stack) {
            sb.append("/" + str);
        }

        return sb.toString();
    }

    public static void createParentDirs(File file) throws IOException {
        checkNotNull(file);
        File parent = file.getCanonicalFile().getParentFile();
        if (parent == null) {
      /*
       * The given directory is a filesystem root. All zero of its ancestors
       * exist. This doesn't mean that the root itself exists -- consider x:\ on
       * a Windows machine without such a drive -- or even that the caller can
       * create it, but this method makes no such guarantees even for non-root
       * files.
       */
            return;
        }
        parent.mkdirs();
        if (!parent.isDirectory()) {
            throw new IOException("Unable to create parent directories of " + file);
        }
    }

}
