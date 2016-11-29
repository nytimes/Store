package com.nytimes.android.external.store.base;

import com.nytimes.android.external.store.base.impl.BarCode;

import rx.Observable;

/**
 * Interface for fetching new data for a Store
 *
 * @param <Raw> data type before parsing
 */
public interface Fetcher<Raw> {

    /**
     * @param barCode Container with Key and Type used as a request param
     * @return Observable that emits {@link Raw} data
     */
    Observable<Raw> fetch(BarCode barCode);
}
