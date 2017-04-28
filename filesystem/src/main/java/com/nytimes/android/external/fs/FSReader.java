package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;

import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Emitter;
import rx.Observable;

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
        return Observable.fromEmitter(emitter -> {
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
                emitter.onError(new FileNotFoundException(ERROR_MESSAGE + resolvedKey));
            }
        }, Emitter.BackpressureMode.NONE);
    }
}
