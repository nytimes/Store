package com.nytimes.android.external.fs;

import com.nytimes.android.external.store.base.DiskErase;
import com.nytimes.android.external.fs.filesystem.FileSystem;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

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

    @Nonnull
    @Override
    public Observable<Boolean> deleteAll(@Nonnull final T key) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Nonnull
            @Override
            @SuppressWarnings("PMD.SignatureDeclareThrowsException")
            public Boolean call() throws Exception {
                fileSystem.deleteAll(pathResolver.resolve(key));
                return true;
            }
        });
    }
}
