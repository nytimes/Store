package com.nytimes.android.external.store.base;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

public interface DiskWrite<Raw, Key> {
    /**
     * @param key to use to get data from persister
     *            If data is not available implementer needs to
     *            either return Observable.empty or throw an exception
     */
    @Nonnull
    Observable<Boolean> write(@Nonnull Key key, @Nonnull Raw raw);
}
