package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.DiskAllErase;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import io.reactivex.Observable;


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
