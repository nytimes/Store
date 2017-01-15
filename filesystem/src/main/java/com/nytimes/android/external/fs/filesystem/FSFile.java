package com.nytimes.android.external.fs.filesystem;

import android.support.annotation.NonNull;

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
    @NonNull
    private final String pathValue;
    @NonNull
    private final File file;

    FSFile(File root, @NonNull String path) throws IOException {
        this.pathValue = path;
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

    @NonNull
    public String path() {
        return pathValue;
    }

    public void write(BufferedSource source) throws IOException {

        File tmpFile = File.createTempFile("new", "tmp", file.getParentFile());
        BufferedSink sink = null;
        try {

            sink = Okio.buffer(Okio.sink(tmpFile));
            sink.writeAll(source);

            if (!tmpFile.renameTo(file)) {
                throw new IOException("unable to move tmp file to " + file.getPath());
            }
        } catch (Exception e) {
            throw new IOException("unable to write to file", e);

        } finally {
            tmpFile.delete();
            if (sink != null) {
                sink.close();
            }
        }
    }


    @NonNull
    public BufferedSource source() throws FileNotFoundException {
        if (file.exists()) {
            return Okio.buffer(Okio.source(file));
        }
        throw new FileNotFoundException(pathValue);
    }
}

