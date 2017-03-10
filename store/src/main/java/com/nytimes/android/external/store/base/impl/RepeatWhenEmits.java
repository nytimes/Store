package com.nytimes.android.external.store.base.impl;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static com.nytimes.android.external.cache.Preconditions.checkNotNull;

/**
 * A Transformer that takes a source observable and re-subscribes to the upstream Observable when
 * it emits.
 */
final class RepeatWhenEmits<T> implements ObservableTransformer<T, T> {

    private final Observable source;

    private RepeatWhenEmits(@Nonnull Observable source) {
        this.source = source;
    }

    @Nonnull
    static <T> RepeatWhenEmits<T> from(@Nonnull Observable source) {
        return new RepeatWhenEmits<>(checkNotNull(source));
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Observable<Object> objectObservable) throws Exception {
                return objectObservable.switchMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Object o) throws Exception {
                        return source;
                    }
                });
            }
        });
    }
}
