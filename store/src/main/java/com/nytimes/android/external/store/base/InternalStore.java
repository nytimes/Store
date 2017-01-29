package com.nytimes.android.external.store.base;


import com.nytimes.android.external.store.base.impl.BarCode;

import org.jetbrains.annotations.NotNull;

import rx.Observable;

/**
 * this interface allows us to mark a {@link Store} as "internal", exposing methods for retrieving data
 * directly from memory or from disk.
 */
public interface InternalStore<Parsed> extends Store<Parsed> {
    @NotNull
    Observable<Parsed> memory(@NotNull final BarCode barCode);
    @NotNull
    Observable<Parsed> disk(@NotNull final BarCode barCode);
}
