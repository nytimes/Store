package com.nytimes.android.external.store3.base.impl.room;

import com.nytimes.android.external.store3.annotations.Experimental;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.MemoryPolicy;
import com.nytimes.android.external.store3.base.impl.StalePolicy;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.base.room.RoomFetcher;
import com.nytimes.android.external.store3.base.room.RoomPersister;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

/**
 * a {@link StoreBuilder StoreBuilder}
 * will return an instance of a store
 * <p>
 * A {@link StoreRoom  Store} can
 * {@link StoreRoom#get(V) Store.get() } cached data or
 * force a call to {@link StoreRoom#fetch(V) Store.fresh() }
 * (skipping cache)
 */
@Experimental
public abstract class StoreRoom<T, V> {

    /**
     * Return an Observable of T for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
     */
    @Nonnull
    public abstract Observable<T> get(@Nonnull V key);

    /**
     * Return an Observable of T for requested Barcode skipping Memory & Disk Cache
     */
    @Nonnull
    public abstract Observable<T> fetch(@Nonnull V key);

    /**
     * purges all entries from memory and disk cache
     * Persister will only be cleared if they implements Clearable
     */
    public abstract void clear();

    /**
     * Purge a particular entry from memory and disk cache.
     * Persister will only be cleared if they implements Clearable
     */
    public abstract void clear(@Nonnull V key);


    public static <Raw, Parsed, Key> StoreRoom<Parsed, Key> from
            (RoomFetcher<Raw, Key> fetcher, RoomPersister<Raw, Parsed, Key> persister) {
        return new RealStoreRoom<>(fetcher, persister);
    }

    public static <Raw, Parsed, Key> StoreRoom<Parsed, Key> from(
            RoomFetcher<Raw, Key> fetcher,
            RoomPersister<Raw, Parsed, Key> persister,
            StalePolicy policy) {
        return new RealStoreRoom<>(fetcher, persister, policy);
    }

    public static <Raw, Parsed, Key> StoreRoom<Parsed, Key> from
            (RoomFetcher<Raw, Key> fetcher, RoomPersister<Raw, Parsed, Key> persister,
             StalePolicy stalePolicy, MemoryPolicy memoryPolicy) {
        return new RealStoreRoom<>(fetcher, persister, memoryPolicy, stalePolicy);
    }
}
