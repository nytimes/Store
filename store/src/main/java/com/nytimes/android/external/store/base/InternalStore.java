package com.nytimes.android.external.store.base;

import android.support.annotation.NonNull;

import com.nytimes.android.external.store.base.impl.BarCode;

import rx.Observable;

/**
 * this interface allows us to mark a {@link Store} as "internal", exposing methods for retrieving data
 * directly from memory or from disk.
 */
public interface InternalStore<Parsed> extends Store<Parsed> {
    Observable<Parsed> memory(@NonNull final BarCode barCode);
    Observable<Parsed> disk(@NonNull final BarCode barCode);
}
