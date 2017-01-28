package com.nytimes.android.external.store.base.beta;


import com.nytimes.android.external.store.base.impl.BarCode;

import org.jetbrains.annotations.NotNull;

import rx.Observable;

/**
 * a {@link com.nytimes.android.external.store.base.impl.StoreBuilder StoreBuilder}
 * will return an instance of a store
 * <p>
 * A {@link Store  Store} can
 * {@link Store#get(BarCode) Store.get() } cached data or
 * force a call to {@link Store#fetch(BarCode) Store.fetch() }
 * (skipping cache)
 */
public interface Store<T, V> {

    /**
     * Return an Observable of T for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
     */
    @NotNull
    Observable<T> get(@NotNull V key);

    /**
     * Return an Observable of T for requested Barcode skipping Memory & Disk Cache
     */
    @NotNull
    Observable<T> fetch(@NotNull V key);

    /**
     * @return an Observable that emits new items when they arrive.
     */
    @NotNull
    Observable<T> stream();

    /**
     * Similar to  {@link Store#get(BarCode) Store.get() }
     * Rather than returning a single response, Stream will stay subscribed for future emissions to the Store
     * NOTE: Stream will continue to get emissions for ANY keyAndRawType not just starting one
     *
     * @deprecated Use {@link Store#stream()}. If you need to start with the first value,
     * use {@code store.stream().startWith(store.get(keyAndRawType))}
     */
    @Deprecated
    @NotNull
    Observable<T> stream(V id);

    /**
     * Clear the memory cache of all entries
     */
    void clearMemory();

    /**
     * Purge a particular entry from memory cache.
     */
    void clearMemory(@NotNull V key);


}
