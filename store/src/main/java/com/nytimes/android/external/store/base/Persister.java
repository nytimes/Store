package com.nytimes.android.external.store.base;

import com.nytimes.android.external.store.base.impl.BarCode;

import rx.Observable;

/**
 * Interface for fetching data from persister
 *
 * @param <Raw> data type before parsing
 */
public interface Persister<Raw> {

    /**
     * @param barCode to use to get data from persister
     *                If data is not available implementer needs to
     *                either return Observable.empty or throw an exception
     */
    Observable<Raw> read(final BarCode barCode);

    /**
     * @param barCode to use to store data to persister
     * @param raw     raw string to be stored
     */
    Observable<Boolean> write(final BarCode barCode, final Raw raw);
}
