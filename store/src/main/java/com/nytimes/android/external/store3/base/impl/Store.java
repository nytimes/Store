package com.nytimes.android.external.store3.base.impl;

import com.nytimes.android.external.store.util.Result;
import com.nytimes.android.external.store3.annotations.Experimental;
import javax.annotation.Nonnull;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * a {@link StoreBuilder StoreBuilder}
 * will return an instance of a store
 * <p>
 * A {@link Store  Store} can
 * {@link Store#get(V) Store.get() } cached data or
 * force a call to {@link Store#fetch(V) Store.fetch() }
 * (skipping cache)
 */
public interface Store<T, V> {

    /**
     * Return an Observable of T for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
     */
    @Nonnull
    Single<T> get(@Nonnull V key);

    /**
     * Return an Observable of {@link Result}<T> for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
     */
    @Nonnull
    Single<Result<T>> getWithResult(@Nonnull V key);

    /**
     * Calls store.get(), additionally will repeat anytime store.clear(barcode) is called
     * WARNING: getRefreshing(barcode) is an endless observable, be careful when combining
     * with operators that expect an OnComplete event
     */
    @Experimental
    Observable<T> getRefreshing(@Nonnull final V key);


    /**
     * Return an Observable of T for requested Barcode skipping Memory & Disk Cache
     */
    @Nonnull
    Single<T> fetch(@Nonnull V key);

    /**
     * Return an Observable of {@link Result}<T> for requested Barcode skipping Memory & Disk Cache
     */
    @Nonnull
    Single<Result<T>> fetchWithResult(@Nonnull V key);

    /**
     * @return an Observable that emits "fresh" new response from the store that hit the fetcher
     * WARNING: stream is an endless observable, be careful when combining
     * with operators that expect an OnComplete event
     */
    @Nonnull
    Observable<T> stream();

    /**
     * Similar to  {@link Store#get(V) Store.get() }
     * Rather than returning a single response,
     * Stream will stay subscribed for future emissions to the Store
     * Errors will be dropped
     *
     */
    @Nonnull
    Observable<T> stream(V key);

    /**
     * Clear the memory cache of all entries
     */
    @Deprecated
    void clearMemory();

    /**
     * Purge a particular entry from memory cache.
     */
    @Deprecated
    void clearMemory(@Nonnull V key);

    /**
     * purges all entries from memory and disk cache
     * Persister will only be cleared if they implements Clearable
     */
    void clear();

    /**
     * Purge a particular entry from memory and disk cache.
     * Persister will only be cleared if they implements Clearable
     */
    void clear(@Nonnull V key);
}
