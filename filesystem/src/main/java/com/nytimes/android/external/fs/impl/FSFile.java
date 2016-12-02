package com.nytimes.android.external.fs.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static dagger.internal.Preconditions.checkNotNull;
import static java.lang.String.format;

class FSFile {

    private final String path;
    private final File file;

    FSFile(File root, String path) throws IOException {
        this.path = path;
        this.file = new File(root, path);
        if (file.exists() && file.isDirectory()) {
            throw new FileNotFoundException(format("expecting a file at %s, instead found a directory", path));
        }
        createParentDirs(this.file);
    }

    public boolean exists() {
        return file.exists();
    }

    public void delete() {
        /**
         * it's ok to delete the file even if we still have readers! the file won't really
         * be deleted until all readers close it (it just removes the name-to-inode mapping)
         */
        if (!file.delete()) {
            throw new IllegalStateException("unable to delete " + file);
        }
    }

    public String path() {
        return path;
    }

    public void write(BufferedSource source) throws IOException {

        File tmpFile = File.createTempFile("new", "tmp", file.getParentFile());
        try {

            BufferedSink sink = Okio.buffer(Okio.sink(tmpFile));
            sink.writeAll(source);
            sink.close();

            if (!tmpFile.renameTo(file)) {
                throw new IOException("unable to move tmp file to " + file.getPath());
            }
        }
        catch (Exception e){
            throw new IOException("unable to write to file");

        } finally {
            tmpFile.delete();
        }
    }


    public BufferedSource source() throws FileNotFoundException {
        return Okio.buffer(Okio.source(file));
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

