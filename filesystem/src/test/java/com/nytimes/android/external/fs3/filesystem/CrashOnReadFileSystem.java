package com.nytimes.android.external.fs3.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.annotation.Nonnull;
import okio.BufferedSource;

public class CrashOnReadFileSystem extends FileSystemImpl {

    public CrashOnReadFileSystem(@Nonnull File root) throws IOException {
        super(root);
    }

    @Nonnull
    @Override
    public BufferedSource read(@Nonnull String path) throws FileNotFoundException {
        if (path.contains("crash")) {
            throw new FileNotFoundException();
        }
        return super.read(path);
    }
}
