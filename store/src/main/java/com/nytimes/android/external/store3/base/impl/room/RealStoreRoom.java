package com.nytimes.android.external.store3.base.impl.room;

import com.nytimes.android.external.cache3.Cache;
import com.nytimes.android.external.store3.annotations.Experimental;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.CacheFactory;
import com.nytimes.android.external.store3.base.impl.MemoryPolicy;
import com.nytimes.android.external.store3.base.impl.StalePolicy;
import com.nytimes.android.external.store3.base.impl.StoreUtil;
import com.nytimes.android.external.store3.base.room.RoomPersister;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.reactivex.Observable;

/**
 * Store to be used for loading an object from different data sources
 *
 * @param <Raw>    data type before parsing, usually a String, Reader or BufferedSource
 * @param <Parsed> data type after parsing
 *                 <p>
 */
@Experimental
class RealStoreRoom<Raw, Parsed, Key> extends StoreRoom<Parsed, Key> {
    private final Fetcher<Raw, Key> fetcher;
    private final RoomPersister<Raw, Parsed, Key> persister;
    private final Cache<Key, Observable<Parsed>> memCache;
    private final StalePolicy stalePolicy;
    private final Cache<Key, Observable<Parsed>> inFlightRequests;


     RealStoreRoom(Fetcher<Raw, Key> fetcher,
                         RoomPersister<Raw, Parsed, Key> persister) {
        this(fetcher, persister, null, StalePolicy.UNSPECIFIED);
    }

     RealStoreRoom(Fetcher<Raw, Key> fetcher,
                         RoomPersister<Raw, Parsed, Key> persister,
                         StalePolicy stalePolicy) {
        this(fetcher, persister, null, stalePolicy);
    }

    RealStoreRoom(Fetcher<Raw, Key> fetcher,
                  RoomPersister<Raw, Parsed, Key> persister,
                  MemoryPolicy memoryPolicy,
                  StalePolicy stalePolicy) {
        this.fetcher = fetcher;
        this.persister = persister;
        this.stalePolicy = stalePolicy;
        this.memCache = CacheFactory.createRoomCache(memoryPolicy);
        this.inFlightRequests = CacheFactory.createRoomInflighter(memoryPolicy);
    }

    /**
     * @param key
     * @return an observable from the first data source that is available
     */
    @Nonnull
    @Override
    public Observable<Parsed> get(@Nonnull final Key key) {
        return lazyCache(key).switchIfEmpty(fetch(key));
    }

    /**
     * @return data from memory
     */
    private Observable<Parsed> lazyCache(@Nonnull final Key key) {
        return Observable.defer(() -> cache(key)).onErrorResumeNext(Observable.empty());
    }

    Observable<Parsed> cache(@Nonnull final Key key) {
        try {
            return memCache.get(key, () -> disk(key));
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }

    @Nonnull
    public Observable<Parsed> memory(@Nonnull Key key) {
        Observable<Parsed> cachedValue = memCache.getIfPresent(key);
        return cachedValue == null ? Observable.empty() : cachedValue;
    }

    /**
     * Fetch data from persister and update memory after. If an error occurs, emit an empty observable
     * so that the concat call in {@link #get(Key)} moves on to {@link #fetch(Key)}
     *
     * @param key
     * @return
     */
    @Nonnull
    public Observable<Parsed> disk(@Nonnull final Key key) {
        if (StoreUtil.shouldReturnNetworkBeforeStale(persister, stalePolicy, key)) {
            return Observable.empty();
        }
        return readDisk(key);
    }

    Observable<Parsed> readDisk(@Nonnull final Key key) {
        return persister()
                .read(key)
                .doOnNext(this::guardAgainstEmptyCollection)
                .onErrorResumeNext(
                        Observable.empty())
                .doOnNext(parsed -> {
                    updateMemory(key, parsed);
                    if (stalePolicy == StalePolicy.REFRESH_ON_STALE
                            && StoreUtil.persisterIsStale(key, persister)) {
                        backfillCache(key);
                    }
                }).cache();
    }

    @SuppressWarnings("CheckReturnValue")
    void backfillCache(@Nonnull Key key) {
        fetch(key).subscribe(it -> {
        }, it -> {
        });
    }


    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to network
     *
     * @return data from fetch and store it in memory and persister
     */
    @Nonnull
    @Override
    public Observable<Parsed> fetch(@Nonnull final Key key) {
        return Observable.defer(() -> fetchAndPersist(key));
    }


    /**
     * There should only be one fetch request in flight at any give time.
     * <p>
     * Return cached request in the form of a Behavior Subject which will emit to its subscribers
     * the last value it gets. Subject/Observable is cached in a {@link ConcurrentMap} to maintain
     * thread safety.
     *
     * @param key resource identifier
     * @return observable that emits a {@link Parsed} value
     */
    @Nullable
    Observable<Parsed> fetchAndPersist(@Nonnull final Key key) {
        try {
            return inFlightRequests.get(key, () -> response(key));
        } catch (ExecutionException e) {
            return Observable.error(e);
        }
    }

    @Nonnull
    Observable<Parsed> response(@Nonnull final Key key) {
        return fetcher()
                .fetch(key)
                .doOnSuccess(it -> persister().write(key, it))
                .flatMapObservable(it -> readDisk(key))
                .onErrorResumeNext(throwable -> {
                    if (stalePolicy == StalePolicy.NETWORK_BEFORE_STALE) {
                        return readDisk(key).switchIfEmpty(Observable.error(throwable));
                    }
                    return Observable.error(throwable);
                })
                .doAfterTerminate(() -> inFlightRequests.invalidate(key))
                .cache();
    }


    /**
     * Only update memory after persister has been successfully updated
     *
     * @param key
     * @param data
     */
    void updateMemory(@Nonnull final Key key, final Parsed data) {
        memCache.put(key, Observable.just(data));
    }


    @Override
    //need to create a clearable override to clear all
    // since room knows what table associated with data
    public void clear() {
        for (Key cachedKey : memCache.asMap().keySet()) {
            clear(cachedKey);
        }
    }

    @Override
    public void clear(@Nonnull Key key) {
        inFlightRequests.invalidate(key);
        memCache.invalidate(key);
        StoreUtil.clearPersister(persister(), key);
    }


    /**
     * @return DiskDAO that stores and stores <Raw> data
     */
    RoomPersister<Raw, Parsed, Key> persister() {
        return persister;
    }

    /**
     *
     */
    Fetcher<Raw, Key> fetcher() {
        return fetcher;
    }

    private void guardAgainstEmptyCollection(Parsed v) {
        if (v instanceof Collection && ((Collection) v).isEmpty()) {
            throw new IllegalStateException("empty result set");
        }
    }
}

