package com.nytimes.android.external.store2.base;

import com.nytimes.android.external.store2.base.impl.Store;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

/**
2 * this interface allows us to mark a {@link Store} as "internal", exposing methods for retrieving data
 * directly from memory or from disk.
 */
public interface InternalStore<Parsed, Key> extends Store<Parsed, Key> {
    @Nonnull
    Observable<Parsed> memory(@Nonnull final Key key);

    @Nonnull
    Observable<Parsed> disk(@Nonnull final Key key);
}
