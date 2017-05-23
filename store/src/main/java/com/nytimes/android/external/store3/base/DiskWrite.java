package com.nytimes.android.external.store3.base;

import javax.annotation.Nonnull;

import io.reactivex.Single;

public interface DiskWrite<Raw, Key> {
    /**
     * @param key to use to get data from persister
     *            If data is not available implementer needs to
     *            either return Observable.empty or throw an exception
     */
    @Nonnull
    Single<Boolean> write(@Nonnull Key key, @Nonnull Raw raw);
}
