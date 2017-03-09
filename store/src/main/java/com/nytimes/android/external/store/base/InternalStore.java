package com.nytimes.android.external.store.base;

import com.nytimes.android.external.store.base.impl.Store;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * this interface allows us to mark a {@link Store} as "internal", exposing methods for retrieving data
 * directly from memoryPolicy or from disk.
 */
public interface InternalStore<Parsed, Key> extends Store<Parsed, Key> {
    @Nonnull
    Observable<Parsed> memory(@Nonnull final Key key);

    @Nonnull
    Observable<Parsed> disk(@Nonnull final Key key);
}
