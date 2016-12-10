package com.nytimes.android.external.fs.impl;

import com.nytimes.android.external.fs.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static java.lang.String.format;

class FSFile {

    private final Util util = new Util();
    private final String path;
    private final File file;

    FSFile(File root, String path) throws IOException {
        this.path = path;
        this.file = new File(root, path);
        if (file.exists() && file.isDirectory()) {
            throw new FileNotFoundException(format("expecting a file at %s, instead found a directory", path));
        }
        util.createParentDirs(this.file);
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
        if (file.exists()) {
            return Okio.buffer(Okio.source(file));
        }
        throw new FileNotFoundException(path);
    }
}

