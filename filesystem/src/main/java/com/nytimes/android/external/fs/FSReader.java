package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Emitter;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * FSReader is used when persisting from file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 *
 * @param <T> key type
 */
public class FSReader<T> implements DiskRead<BufferedSource, T> {
    private static final String ERROR_MESSAGE = "resolvedKey does not resolve to a file";
    final FileSystem fileSystem;
    final PathResolver<T> pathResolver;

    public FSReader(FileSystem fileSystem, PathResolver<T> pathResolver) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> read(@Nonnull final T key) {
        return Observable.fromEmitter(new Action1<Emitter<BufferedSource>>() {
            @Override
            public void call(Emitter<BufferedSource> emitter) {
                String resolvedKey = pathResolver.resolve(key);
                boolean exists = fileSystem.exists(resolvedKey);

                if (exists) {
                    try {
                        BufferedSource bufferedSource = fileSystem.read(resolvedKey);
                        emitter.onNext(bufferedSource);
                        emitter.onCompleted();
                    } catch (FileNotFoundException e) {
                        emitter.onError(e);
                    }
                } else {
                    emitter.onCompleted();
                }
            }
        }, Emitter.BackpressureMode.NONE);
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> readAll(@Nonnull final T type) throws FileNotFoundException {
        return Observable.defer(new Func0<Observable<BufferedSource>>() {
            @Override
            public Observable<BufferedSource> call() {
                Observable<BufferedSource> bufferedSourceObservable = null;
                try {
                    bufferedSourceObservable = Observable
                            .from(fileSystem.list(pathResolver.resolve(type)))
                            .map(new Func1<String, BufferedSource>() {
                                @Override
                                public BufferedSource call(String s) {
                                    try {
                                        return fileSystem.read(s);
                                    } catch (FileNotFoundException e) {
                                        throw Exceptions.propagate(e);
                                    }
                                }
                            });
                } catch (FileNotFoundException e) {
                    throw Exceptions.propagate(e);
                }
                return bufferedSourceObservable;
            }
        });
    }
}
