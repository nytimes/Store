package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;

import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import okio.BufferedSource;
import rx.Emitter;
import rx.functions.Action1;

/**
 * FSReader is used when persisting from file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 *
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
    public Observable<BufferedSource> read(@Nonnull final T key) {
        return RxJavaInterop.toV2Observable(rx.Observable.fromEmitter(new Action1<Emitter<BufferedSource>>() {
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
        }, Emitter.BackpressureMode.NONE));
    }
}
