package com.nytimes.android.external.fs;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;

/**
 * This Transformer applies {@code map} to the source {@link rx.Observable} in order to transform it from one that
 * emits objects of type {@code Parsed} to one that emits {@link okio.BufferedSource} of those objects.
 * <p>
 * @param <Parsed> the type of objects emitted by the source observable.
 */
public class ObjectToSourceTransformer<Parsed> implements Observable.Transformer<Parsed, BufferedSource> {

    @Nonnull
    protected BufferedSourceAdapter<Parsed> adapter;

    public ObjectToSourceTransformer(@Nonnull BufferedSourceAdapter<Parsed> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Observable<BufferedSource> call(Observable<Parsed> objectObservable) {
        return objectObservable.map(object -> adapter.toJson(object));
    }
}
