package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;

public class FSReader<T> implements DiskRead<BufferedSource,T> {
     final FileSystem fileSystem;
     final String filenamePrefix;

    public FSReader(FileSystem fileSystem, String filenamePrefix) {
        this.fileSystem = fileSystem;
        this.filenamePrefix = filenamePrefix;
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> read(final T id) {
        return fileSystem.exists(filenamePrefix + id) ?
                Observable.fromCallable(new Callable<BufferedSource>() {
                    @Override
                    public BufferedSource call() throws Exception {
                        return fileSystem.read(filenamePrefix + id);
                    }
                }) :
                Observable.<BufferedSource>empty();
    }

    boolean exists(String path) {
        return fileSystem.exists(path);
    }
}
