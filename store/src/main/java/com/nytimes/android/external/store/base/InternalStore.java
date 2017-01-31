package com.nytimes.android.external.store.base;


import com.nytimes.android.external.store.base.impl.BarCode;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * this interface allows us to mark a {@link Store} as "internal", exposing methods for retrieving data
 * directly from memory or from disk.
 */
public interface InternalStore<Parsed> extends Store<Parsed> {
    @Nonnull
    Observable<Parsed> memory(@Nonnull final BarCode barCode);
    @Nonnull
    Observable<Parsed> disk(@Nonnull final BarCode barCode);
}
