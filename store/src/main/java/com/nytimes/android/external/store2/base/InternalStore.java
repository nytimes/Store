package com.nytimes.android.external.store2.base;

import com.nytimes.android.external.store2.base.impl.Store;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;

/**
2 * this interface allows us to mark a {@link Store} as "internal", exposing methods for retrieving data
 * directly from memory or from disk.
 */
public interface InternalStore<Parsed, Key> extends Store<Parsed, Key> {
    @Nonnull
    Maybe<Parsed> memory(@Nonnull final Key key);

    @Nonnull
    Maybe<Parsed> disk(@Nonnull final Key key);
}
