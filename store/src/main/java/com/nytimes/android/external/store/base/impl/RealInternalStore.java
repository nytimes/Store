package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store.base.Clearable;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.util.KeyParser;
import com.nytimes.android.external.store.util.OnErrorResumeWithEmpty;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import rx.Observable;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static com.nytimes.android.external.store.base.impl.StoreUtil.persisterIsStale;
import static com.nytimes.android.external.store.base.impl.StoreUtil.shouldReturnNetworkBeforeStale;

/**
 * Store to be used for loading an object different data sources
 *
 * @param <Raw>    data type before parsing usually String, Reader or BufferedSource
 * @param <Parsed> data type after parsing
 *                 <p>
 *                 Example usage:  @link
 */

final class RealInternalStore<Raw, Parsed, Key> implements InternalStore<Parsed, Key> {
    Cache<Key, Observable<Parsed>> inFlightRequests;
    Cache<Key, Observable<Parsed>> memCache;
    StalePolicy stalePolicy;
    Persister<Raw, Key> persister;
    KeyParser<Key, Raw, Parsed> parser;

    private final PublishSubject<Key> refreshSubject = PublishSubject.create();
    private Fetcher<Raw, Key> fetcher;
    private BehaviorSubject<Parsed> subject;


    RealInternalStore(Fetcher<Raw, Key> fetcher,
                      Persister<Raw, Key> persister,
                      KeyParser<Key, Raw, Parsed> parser,
                      Cache<Key, Observable<Parsed>> memCache,
                      StalePolicy stalePolicy) {
        init(fetcher, persister, parser, memCache, stalePolicy);
    }


    RealInternalStore(Fetcher<Raw, Key> fetcher,
                      Persister<Raw, Key> persister,
                      KeyParser<Key, Raw, Parsed> parser,
                      StalePolicy stalePolicy) {
        memCache = CacheBuilder.newBuilder()
                .maximumSize(getCacheSize())
                .expireAfterAccess(getCacheTTL(), TimeUnit.SECONDS)
                .build();
        init(fetcher, persister, parser, memCache, stalePolicy);

    }

    private void init(Fetcher<Raw, Key> fetcher,
                      Persister<Raw, Key> persister,
                      KeyParser<Key, Raw, Parsed> parser,
                      Cache<Key, Observable<Parsed>> memCache, StalePolicy stalePolicy) {
        this.fetcher = fetcher;
        this.persister = persister;
        this.parser = parser;
        this.memCache = memCache;
        this.stalePolicy = stalePolicy;
        inFlightRequests = CacheBuilder.newBuilder()
                .expireAfterWrite(TimeUnit.MINUTES.toSeconds(1), TimeUnit.SECONDS)
                .build();

        subject = BehaviorSubject.create();
    }


    /**
     * @param key
     * @return an observable from the first data source that is available
     */
    @Nonnull
    @Override
    public Observable<Parsed> get(@Nonnull final Key key) {
        return Observable.concat(
                lazyCache(key),
                fetch(key)
        ).take(1);
    }

    @Override
    @Nonnull
    @Experimental
    public Observable<Parsed> getRefreshing(@Nonnull final Key key) {
        return get(key)
                .compose(StoreUtil.<Parsed, Key>repeatWhenCacheEvicted(refreshSubject, key));
    }


    /**
     * @return data from memory
     */
    private Observable<Parsed> lazyCache(@Nonnull final Key key) {
        return Observable
                .defer(new Func0<Observable<Parsed>>() {
                    @Override
                    public Observable<Parsed> call() {
                        return cache(key);
                    }
                })
                .onErrorResumeNext(new OnErrorResumeWithEmpty<Parsed>());
    }

    Observable<Parsed> cache(@Nonnull final Key key) {
        try {
            return memCache.get(key, new Callable<Observable<Parsed>>() {
                @Nonnull
                @Override
                @SuppressWarnings("PMD.SignatureDeclareThrowsException")
                public Observable<Parsed> call() throws Exception {
                    return disk(key);
                }
            });
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }


    @Nonnull
    @Override
    public Observable<Parsed> memory(@Nonnull Key key) {
        Observable<Parsed> cachedValue = memCache.getIfPresent(key);
        return cachedValue == null ? Observable.<Parsed>empty() : cachedValue;
    }

    /**
     * Fetch data from persister and update memory after. If an error occurs, emit and empty observable
     * so that the concat call in {@link #get(Key)} moves on to {@link #fetch(Key)}
     *
     * @param key
     * @return
     */
    @Nonnull
    @Override
    public Observable<Parsed> disk(@Nonnull final Key key) {
        if (shouldReturnNetworkBeforeStale(persister, stalePolicy, key)) {
            return Observable.empty();
        }

        return readDisk(key);
    }

    Observable<Parsed> readDisk(@Nonnull final Key key) {
        return persister().read(key)
                .onErrorResumeNext(new OnErrorResumeWithEmpty<Raw>())
                .map(new Func1<Raw, Parsed>() {
                    @Override
                    public Parsed call(Raw raw) {
                        return parser.call(key, raw);
                    }
                })
                .doOnNext(new Action1<Parsed>() {
                    @Override
                    public void call(Parsed parsed) {
                        updateMemory(key, parsed);
                        if (stalePolicy == StalePolicy.REFRESH_ON_STALE
                                && persisterIsStale(key, persister)) {
                            backfillCache(key);
                        }
                    }
                }).cache();
    }

    void backfillCache(@Nonnull Key key) {
        fetch(key).subscribe(new Action1<Parsed>() {
            @Override
            public void call(Parsed parsed) {
                //do Nothing we are just backfilling cache
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //do nothing as we are just backfilling cache
            }
        });
    }


    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to nerwork
     *
     * @return data from fetch and store it in memory and persister
     */
    @Nonnull
    @Override
    public Observable<Parsed> fetch(@Nonnull final Key key) {
        return Observable.defer(new Func0<Observable<Parsed>>() {
            @Nullable
            @Override
            public Observable<Parsed> call() {
                return fetchAndPersist(key);
            }
        });
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
            return inFlightRequests.get(key, new Callable<Observable<Parsed>>() {
                @Nonnull
                @Override
                public Observable<Parsed> call() {
                    return response(key);
                }
            });
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }

    @Nonnull
    Observable<Parsed> response(@Nonnull final Key key) {
        return fetcher()
                .fetch(key)
                .flatMap(new Func1<Raw, Observable<Parsed>>() {
                    @Override
                    public Observable<Parsed> call(Raw raw) {
                        return persister().write(key, raw)
                                .flatMap(new Func1<Boolean, Observable<Parsed>>() {
                                    @Override
                                    public Observable<Parsed> call(Boolean aBoolean) {
                                        return readDisk(key);
                                    }
                                });
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Parsed>>() {
                    @Override
                    public Observable<? extends Parsed> call(Throwable throwable) {
                        if (stalePolicy == StalePolicy.NETWORK_BEFORE_STALE) {
                            return readDisk(key);
                        }
                        return Observable.error(throwable);
                    }
                })
                .doOnNext(new Action1<Parsed>() {
                    @Override
                    public void call(Parsed data) {
                        notifySubscribers(data);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        inFlightRequests.invalidate(key);
                    }
                })
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

        Observable<Parsed> stream = subject.asObservable();

        //If nothing was emitted through the subject yet, start stream with get() value
        if (!subject.hasValue()) {
            return stream.startWith(get(key));
        }

        return stream;
    }

    @Nonnull
    @Override
    public Observable<Parsed> stream() {
        return subject.asObservable();
    }

    /**
     * Only update memory after persister has been successfully update
     *
     * @param key
     * @param data
     */
    void updateMemory(@Nonnull final Key key, final Parsed data) {
        memCache.put(key, Observable.just(data));
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
        clearPersister(key);
        notifyRefresh(key);
    }

    private void notifyRefresh(Key key) {
        refreshSubject.onNext(key);
    }

    private void clearPersister(Key key) {
        boolean isPersisterClearable = persister instanceof Clearable;

        if (isPersisterClearable) {
            ((Clearable<Key>) persister).clear(key);
        }
    }

    /**
     * Default Cache TTL, can be overridden
     *
     * @return memory persister ttl
     */
    private long getCacheTTL() {
        return TimeUnit.HOURS.toSeconds(24);
    }

    /**
     * Default mem persister is 1, can be overridden otherwise
     *
     * @return memory persister size
     */
    private long getCacheSize() {
        return 100;
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

