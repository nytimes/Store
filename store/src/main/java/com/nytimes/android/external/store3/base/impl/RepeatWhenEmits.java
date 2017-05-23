package com.nytimes.android.external.store3.base.impl;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

import static com.nytimes.android.external.cache3.Preconditions.checkNotNull;

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
        return upstream.repeatWhen(events -> events.switchMap(aVoid -> source));
    }
}
