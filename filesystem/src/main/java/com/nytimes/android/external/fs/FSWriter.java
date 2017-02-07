package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskWrite;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;

public class FSWriter<T> implements DiskWrite<BufferedSource, T> {
    final FileSystem fileSystem;
    final String filenamePrefix;

    public FSWriter(FileSystem fileSystem, String filenamePrefix) {
        this.fileSystem = fileSystem;
        this.filenamePrefix = filenamePrefix;
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(final T barCode, final BufferedSource data) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Nonnull
            @Override
            @SuppressWarnings("PMD.SignatureDeclareThrowsException")
            public Boolean call() throws Exception {
                fileSystem.write(filenamePrefix + barCode, data);
                return true;
            }
        });
    }
}
