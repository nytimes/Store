package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;

/**
 * FSReader is used when persisting from file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 * @param <T> key type
 */
public class FSReader<T> implements DiskRead<BufferedSource, T> {
    final FileSystem fileSystem;
    final PathResolver<T> pathResolver;

    public FSReader(FileSystem fileSystem, PathResolver<T> pathResolver) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> read(final T id) {
        return fileSystem.exists(pathResolver.resolve(id)) ?
                Observable.fromCallable(new Callable<BufferedSource>() {
                    @Override
                    public BufferedSource call() throws FileNotFoundException {
                        return fileSystem.read(pathResolver.resolve(id));
                    }
                }) :
                Observable.<BufferedSource>empty();
    }
}
