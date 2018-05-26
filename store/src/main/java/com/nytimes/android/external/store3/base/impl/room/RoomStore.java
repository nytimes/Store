package com.nytimes.android.external.store3.base.impl.room;

import com.nytimes.android.external.store3.annotations.Experimental;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

/**
 * a {@link StoreBuilder StoreBuilder}
 * will return an instance of a store
 * <p>
 * A {@link RoomStore  Store} can
 * {@link RoomStore#get(V) Store.get() } cached data or
 * force a call to {@link RoomStore#fetch(V) Store.fetch() }
 * (skipping cache)
 */
@Experimental
public interface RoomStore<T, V> {

    /**
     * Return an Observable of T for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
     */
    @Nonnull
    Observable<T> get(@Nonnull V key);

    /**
     * Return an Observable of T for requested Barcode skipping Memory & Disk Cache
     */
    @Nonnull
    Observable<T> fetch(@Nonnull V key);

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
