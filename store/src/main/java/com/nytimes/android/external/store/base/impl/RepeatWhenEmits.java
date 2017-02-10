package com.nytimes.android.external.store.base.impl;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.functions.Func1;

import static com.nytimes.android.external.cache.Preconditions.checkNotNull;

/**
 * A Transformer that takes a source observable and re-subscribes to the upstream Observable when
 * it emits.
 */
final class RepeatWhenEmits<T> implements Observable.Transformer<T, T> {

    private final Observable source;

    private RepeatWhenEmits(@Nonnull Observable source) {
        this.source = source;
    }

    @Nonnull
    static <T> RepeatWhenEmits<T> from(@Nonnull Observable source) {
        return new RepeatWhenEmits<>(checkNotNull(source));
    }

    @Override
    public Observable<T> call(Observable<T> upstream) {
        return upstream.repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Void> events) {
                return events.switchMap(new Func1<Void, Observable<?>>() {
                    @Override
                    public Observable<?> call(Void aVoid) {
                        return source;
                    }
                });
            }
        });
    }
}