package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store.util.Result
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * a [StoreBuilder]
 * will return an instance of a store
 *
 *
 * A [Store] can
 * [Store.get() ][Store.get] cached data or
 * force a call to [Store.fresh() ][Store.fresh]
 * (skipping cache)
 */
interface Store<T, V> {

    /**
     * Return an Observable of T for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
     */
    suspend fun get(key: V): T

    /**
     * Return an Observable of [Result]<T> for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
    </T> */
//    suspend fun getWithResult(key: V): Result<T>

    /**
     * Calls store.get(), additionally will repeat anytime store.clear(barcode) is called
     * WARNING: getRefreshing(barcode) is an endless observable, be careful when combining
     * with operators that expect an OnComplete event
     */
//    @Experimental
//    fun getRefreshing(key: V): Observable<T>


    /**
     * Return an Observable of T for requested Barcode skipping Memory & Disk Cache
     */
    suspend fun fresh(key: V): T

    /**
     * Return an Observable of [Result]<T> for requested Barcode skipping Memory & Disk Cache
    </T> */
//    suspend fun freshWithResult(key: V): Result<T>

    /**
     * @return an Observable that emits "fresh" new response from the store that hit the fetcher
     * WARNING: stream is an endless observable, be careful when combining
     * with operators that expect an OnComplete event
     */
    // TODO should this method return a Pair<K, T> ?
    fun stream(): ReceiveChannel<T>

    /**
     * Similar to  [Store.get() ][Store.get]
     * Rather than returning a single response,
     * Stream will stay subscribed for future emissions to the Store
     * Errors will be dropped
     *
     */
    fun stream(key: V): ReceiveChannel<T>

    /**
     * Clear the memory cache of all entries
     */
    @Deprecated("")
    fun clearMemory()

    /**
     * Purge a particular entry from memory cache.
     */
    @Deprecated("")
    fun clearMemory(key: V)

    /**
     * purges all entries from memory and disk cache
     * Persister will only be cleared if they implements Clearable
     */
    fun clear()

    /**
     * Purge a particular entry from memory and disk cache.
     * Persister will only be cleared if they implements Clearable
     */
    fun clear(key: V)
}
