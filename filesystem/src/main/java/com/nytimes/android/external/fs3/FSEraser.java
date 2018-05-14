package com.nytimes.android.external.fs3;


import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.DiskErase;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import io.reactivex.Single;

public class FSEraser<T> implements DiskErase<T> {
    final FileSystem fileSystem;
    final PathResolver<T> pathResolver;

    public FSEraser(FileSystem fileSystem, PathResolver<T> pathResolver) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
    }

    @Nonnull
    @Override
    public Single<Boolean> delete(final @Nonnull T key) {
        return Single.fromCallable(new Callable<Boolean>() {
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
