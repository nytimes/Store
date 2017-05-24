package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskAllErase;
import com.nytimes.android.external.store.base.DiskErase;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;


public class FSAllEraser implements DiskAllErase {
    final FileSystem fileSystem;

    public FSAllEraser(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    @Nonnull
    @Override
    public Observable<Boolean> deleteAll(@Nonnull final String path) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Nonnull
            @Override
            @SuppressWarnings("PMD.SignatureDeclareThrowsException")
            public Boolean call() throws Exception {
                fileSystem.deleteAll(path);
                return true;
            }
        });
    }
}
