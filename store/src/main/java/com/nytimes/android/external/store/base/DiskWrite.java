package com.nytimes.android.external.store.base;

import javax.annotation.Nonnull;

import rx.Observable;

public interface DiskWrite<Raw, Key> {
    /**
     * @param barCode to use to get data from persister
     *                If data is not available implementer needs to
     *                either return Observable.empty or throw an exception
     */
    @Nonnull
    Observable<Boolean> write(Key barCode, Raw raw);
}
