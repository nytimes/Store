package com.nytimes.android.external.store3.base;

import com.nytimes.android.external.store3.base.impl.Store;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.reactivex.Maybe;

/**
 * This interface allows us to mark a {@link Store} as "internal", exposing methods for retrieving data
 * directly from memory or from disk.
 */
public interface InternalStore<Parsed, Key> extends Store<Parsed, Key> {
    @Nonnull
    Maybe<Parsed> memory(@Nonnull final Key key);

    @Nonnull
    Maybe<Parsed> disk(@Nonnull final Key key);

    /**
     * @return The given persister for that store. Or {@code null} if not added.
     * @see com.nytimes.android.external.store3.base.impl.RealStoreBuilder#persister
     */
    @Nullable
    Persister<?, Key> persister();

    /**
     * @return The given fetcher for that store. Or {@code null} if not added.
     * @see com.nytimes.android.external.store3.base.impl.RealStoreBuilder#fetcher
     */
    @Nullable
    Fetcher<?, Key> fetcher();
}
