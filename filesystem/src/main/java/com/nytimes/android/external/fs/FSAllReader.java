package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskAllRead;
import com.nytimes.android.external.store.base.DiskRead;

import java.io.FileNotFoundException;

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
 */
public class FSAllReader implements DiskAllRead {
    final FileSystem fileSystem;

    public FSAllReader(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    @Nonnull
    @Override
    public Observable<BufferedSource> readAll(@Nonnull final String path) throws FileNotFoundException {
        return Observable.defer(new Func0<Observable<BufferedSource>>() {
            @Override
            public Observable<BufferedSource> call() {
                Observable<BufferedSource> bufferedSourceObservable = null;
                try {
                    bufferedSourceObservable = Observable
                            .from(fileSystem.list(path))
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
