package com.nytimes.android.external.store.base;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * Interface for fetching data from persister
 *
 * @param <Raw> data type before parsing
 */
public interface Persister<Raw, Key> {

    /**
     * @param barCode to use to get data from persister
     *                If data is not available implementer needs to
     *                either return Observable.empty or throw an exception
     */
    @Nonnull
    Observable<Raw> read(final Key barCode);

    /**
     * @param barCode to use to store data to persister
     * @param raw     raw string to be stored
     */
    @Nonnull
    Observable<Boolean> write(final Key barCode, final Raw raw);
}
