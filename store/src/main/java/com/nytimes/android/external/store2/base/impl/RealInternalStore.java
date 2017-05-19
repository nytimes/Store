package com.nytimes.android.external.store2.base.impl;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store2.base.Fetcher;
import com.nytimes.android.external.store2.base.InternalStore;
import com.nytimes.android.external.store2.base.Persister;
import com.nytimes.android.external.store2.util.KeyParser;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.Experimental;
import io.reactivex.subjects.PublishSubject;

/**
 * Store to be used for loading an object from different data sources
 *
 * @param <Raw>    data type before parsing, usually a String, Reader or BufferedSource
 * @param <Parsed> data type after parsing
 *                 <p>
 *                 Example usage:  @link
 */
final class RealInternalStore<Raw, Parsed, Key> implements InternalStore<Parsed, Key> {
    Cache<Key, Single<Parsed>> inFlightRequests;
    Cache<Key, Maybe<Parsed>> memCache;
    StalePolicy stalePolicy;
    Persister<Raw, Key> persister;
    KeyParser<Key, Raw, Parsed> parser;

    private final PublishSubject<Key> refreshSubject = PublishSubject.create();
    private Fetcher<Raw, Key> fetcher;
    private PublishSubject<Parsed> subject;

    RealInternalStore(Fetcher<Raw, Key> fetcher,
                      Persister<Raw, Key> persister,
                      KeyParser<Key, Raw, Parsed> parser,
                      StalePolicy stalePolicy) {
        this(fetcher, persister, parser, null, stalePolicy);
    }

    RealInternalStore(Fetcher<Raw, Key> fetcher,
                      Persister<Raw, Key> persister,
                      KeyParser<Key, Raw, Parsed> parser,
                      MemoryPolicy memoryPolicy,
                      StalePolicy stalePolicy) {

        this.fetcher = fetcher;
        this.persister = persister;
        this.parser = parser;
        this.stalePolicy = stalePolicy;

        this.memCache = CacheFactory.createCache(memoryPolicy);
        this.inFlightRequests = CacheFactory.createInflighter(memoryPolicy);

        subject = PublishSubject.create();
    }

    /**
     * @param key
     * @return an observable from the first data source that is available
     */
    @Nonnull
    @Override
    public Single<Parsed> get(@Nonnull final Key key) {
        return lazyCache(key)
                .switchIfEmpty(fetch(key).toMaybe())
                .toSingle();
    }

    @Override
    @Nonnull
    @Experimental
    public Observable<Parsed> getRefreshing(@Nonnull final Key key) {
        return get(key)
                .toObservable()
                .compose(StoreUtil.<Parsed, Key>repeatWhenCacheEvicted(refreshSubject, key));
    }


    /**
     * @return data from memory
     */
    private Maybe<Parsed> lazyCache(@Nonnull final Key key) {
        return Maybe
                .defer(() -> cache(key))
                .onErrorResumeNext(Maybe.<Parsed>empty());
    }

    Maybe<Parsed> cache(@Nonnull final Key key) {
        try {
            return memCache.get(key, () -> disk(key));
        } catch (ExecutionException e) {
            return Maybe.empty();
        }
    }


    @Nonnull
    @Override
    public Maybe<Parsed> memory(@Nonnull Key key) {
        Maybe<Parsed> cachedValue = memCache.getIfPresent(key);
        return cachedValue == null ? Maybe.<Parsed>empty() : cachedValue;
    }

    /**
     * Fetch data from persister and update memory after. If an error occurs, emit an empty observable
     * so that the concat call in {@link #get(Key)} moves on to {@link #fetch(Key)}
     *
     * @param key
     * @return
     */
    @Nonnull
    @Override
    public Maybe<Parsed> disk(@Nonnull final Key key) {
        if (StoreUtil.shouldReturnNetworkBeforeStale(persister, stalePolicy, key)) {
            return Maybe.empty();
        }

        return readDisk(key);
    }

    Maybe<Parsed> readDisk(@Nonnull final Key key) {
        return persister().read(key)
                .onErrorResumeNext(Maybe.<Raw>empty())
                .map(raw -> parser.apply(key, raw))
                .doOnSuccess(parsed -> {
                    updateMemory(key, parsed);
                    if (stalePolicy == StalePolicy.REFRESH_ON_STALE
                            && StoreUtil.persisterIsStale(key, persister)) {
                        backfillCache(key);
                    }
                }).cache();
    }

    @SuppressWarnings("CheckReturnValue")
    void backfillCache(@Nonnull Key key) {
        fetch(key).subscribe(parsed -> {
            // do Nothing we are just backfilling cache
        }, throwable -> {
            // do nothing as we are just backfilling cache
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
    public Single<Parsed> fetch(@Nonnull final Key key) {
        return Single.defer(() -> fetchAndPersist(key));
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
    Single<Parsed> fetchAndPersist(@Nonnull final Key key) {
        try {
            return inFlightRequests.get(key, () -> response(key));
        } catch (ExecutionException e) {
            return Single.error(e);
        }
    }

    @Nonnull
    Single<Parsed> response(@Nonnull final Key key) {
        return fetcher()
                .fetch(key)
                .flatMap(raw -> persister()
                        .write(key, raw)
                        .flatMap(aBoolean -> readDisk(key).toSingle()))
                .onErrorResumeNext(throwable -> {
                    if (stalePolicy == StalePolicy.NETWORK_BEFORE_STALE) {
                        return readDisk(key)
                                .switchIfEmpty(Maybe.<Parsed>error(throwable))
                                .toSingle();
                    }
                    return Single.error(throwable);
                })
                .doOnSuccess(this::notifySubscribers)
                .doAfterTerminate(() -> inFlightRequests.invalidate(key))
                .cache();
    }

    void notifySubscribers(Parsed data) {
        subject.onNext(data);
    }

    /**
     * Get data stream for Subjects with the argument id
     *
     * @return
     */
    @Nonnull
    @Override
    public Observable<Parsed> stream(@Nonnull Key key) {
        return subject.hide().startWith(get(key).toObservable());
    }

    @Nonnull
    @Override
    public Observable<Parsed> stream() {
        return subject.hide();
    }

    /**
     * Only update memory after persister has been successfully updated
     *
     * @param key
     * @param data
     */
    void updateMemory(@Nonnull final Key key, final Parsed data) {
        memCache.put(key, Maybe.just(data));
    }

    @Override
    @Deprecated
    public void clearMemory() {
        clear();
    }

    /**
     * Clear memory by id
     *
     * @param key of data to clear
     */
    @Override
    @Deprecated
    public void clearMemory(@Nonnull final Key key) {
        clear(key);
    }


    @Override
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
        notifyRefresh(key);
    }

    private void notifyRefresh(@Nonnull Key key) {
        refreshSubject.onNext(key);
    }

    /**
     * @return DiskDAO that stores and stores <Raw> data
     */
    Persister<Raw, Key> persister() {
        return persister;
    }

    /**
     *
     */
    Fetcher<Raw, Key> fetcher() {
        return fetcher;
    }
}

