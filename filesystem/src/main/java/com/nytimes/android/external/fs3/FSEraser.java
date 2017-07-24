package com.nytimes.android.external.fs3;


import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.DiskErase;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import okio.BufferedSource;

public class FSEraser<T> implements DiskErase<BufferedSource, T> {
    final FileSystem fileSystem;
    final PathResolver<T> pathResolver;

    public FSEraser(FileSystem fileSystem, PathResolver<T> pathResolver) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
    }

    @Nonnull
    @Override
    public Observable<Boolean> delete(final @Nonnull T key) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Nonnull
            @Override
            @SuppressWarnings("PMD.SignatureDeclareThrowsException")
            public Boolean call() throws Exception {
                fileSystem.delete(pathResolver.resolve(key));
                return true;
            }
        });
    }
}
