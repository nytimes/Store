package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.DiskWrite;

import javax.annotation.Nonnull;

import io.reactivex.Single;
import okio.BufferedSource;

/**
 * FSReader is used when persisting to file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 * @param <T> key type
 */
public class FSWriter<T> implements DiskWrite<BufferedSource, T> {
    final FileSystem fileSystem;
    final PathResolver<T> pathResolver;

    public FSWriter(FileSystem fileSystem, PathResolver<T> pathResolver) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
    }

    @Nonnull
    @Override
    public Single<Boolean> write(@Nonnull final T key, @Nonnull final BufferedSource data) {
        return Single.fromCallable(() -> {
            fileSystem.write(pathResolver.resolve(key), data);
            return true;
        });
    }
}
