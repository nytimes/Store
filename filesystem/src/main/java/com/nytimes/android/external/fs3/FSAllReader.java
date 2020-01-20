package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.DiskAllRead;

import com.nytimes.android.external.store3.base.ReadResult;
import com.nytimes.android.external.store3.base.ReadResultFactory;
import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.exceptions.Exceptions;
import okio.BufferedSource;

/**
 * FSReader is used when persisting from file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 */
public class FSAllReader implements DiskAllRead {
    final FileSystem fileSystem;

    public FSAllReader(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Nonnull
    @Override
    public Observable<ReadResult<BufferedSource>> safeReadAll(@Nonnull final String path) {
        return Observable.defer(() -> {
            Observable<ReadResult<BufferedSource>> bufferedSourceObservable = null;
            try {
                bufferedSourceObservable = Observable
                    .fromIterable(fileSystem.list(path))
                    .flatMap(s ->
                        Observable.defer(() -> Observable.just(fileSystem.read(s)))
                            .map(ReadResultFactory::createSuccessResult)
                            .onErrorReturn(ReadResultFactory::createFailureResult));
            } catch (FileNotFoundException e) {
                throw Exceptions.propagate(e);
            }
            return bufferedSourceObservable;
        });
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> readAll(@Nonnull final String path) throws FileNotFoundException {
        return Observable.defer(() -> {
            Observable<BufferedSource> bufferedSourceObservable = null;
            try {
                bufferedSourceObservable = Observable
                        .fromIterable(fileSystem.list(path))
                        .map(s -> {
                            try {
                                return fileSystem.read(s);
                            } catch (FileNotFoundException e) {
                                throw Exceptions.propagate(e);
                            }
                        });
            } catch (FileNotFoundException e) {
                throw Exceptions.propagate(e);
            }
            return bufferedSourceObservable;
        });
    }
}
