package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.util.NoopPersister;
import com.nytimes.android.external.store.util.OnErrorResumeWithEmpty;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import rx.Observable;
import rx.annotations.Experimental;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static java.util.Objects.requireNonNull;

/**
 * Store to be used for loading an object different data sources
 *
 * @param <Raw>    data type before parsing usually String, Reader or BufferedSource
 * @param <Parsed> data type after parsing
 *                 <p>
 *                 Example usage:  @link
 */

@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
final class RealInternalStore<Raw, Parsed> implements InternalStore<Parsed> {

    public static final BarCode ALL_BARCODE = new BarCode("all", "all");
    Cache<BarCode, Observable<Parsed>> inFlightRequests;
    Cache<BarCode, Observable<Parsed>> memCache;

    private Fetcher<Raw> fetcher;
    private Persister<Raw> persister;
    private Func1<Raw, Parsed> parser;
    private BehaviorSubject<Parsed> subject;
    private PublishSubject<BarCode> refreshSubject = PublishSubject.create();

    RealInternalStore(Fetcher<Raw> fetcher,
                      Persister<Raw> persister,
                      Func1<Raw, Parsed> parser) {
        memCache = CacheBuilder.newBuilder()
                .maximumSize(getCacheSize())
                .expireAfterAccess(getCacheTTL(), TimeUnit.SECONDS)
                .build();
        init(fetcher, persister, parser, memCache);
    }


    RealInternalStore(Fetcher<Raw> fetcher,
                      Persister<Raw> persister,
                      Func1<Raw, Parsed> parser,
                      Cache<BarCode, Observable<Parsed>> memCache) {
        init(fetcher, persister, parser, memCache);
    }

    private void init(Fetcher<Raw> fetcher,
                      Persister<Raw> persister,
                      Func1<Raw, Parsed> parser,
                      Cache<BarCode, Observable<Parsed>> memCache) {
        this.fetcher = fetcher;
        this.persister = persister;
        this.parser = parser;
        this.memCache = memCache;
        inFlightRequests = CacheBuilder.newBuilder()
                .expireAfterWrite(TimeUnit.MINUTES.toSeconds(1), TimeUnit.SECONDS)
                .build();

        subject = BehaviorSubject.create();
    }


    /**
     * @param barCode
     * @return an observable from the first data source that is available
     */
    @Nonnull
    @Override
    public Observable<Parsed> get(@Nonnull final BarCode barCode) {
        return Observable.concat(
                lazyCache(barCode),
                fetch(barCode)
        ).take(1);
    }

    @Nonnull
    @Experimental
    public Observable<Parsed> getRefreshing(@Nonnull final BarCode barCode) {
        return get(barCode).compose(repeatWhenCacheEvicted(barCode));
    }

    @Nonnull
    private Observable.Transformer<Parsed, Parsed> repeatWhenCacheEvicted(@Nonnull final BarCode key) {
        Observable<BarCode> filter = refreshSubject.filter(new Func1<BarCode, Boolean>() {
            @Override
            public Boolean call(BarCode barCode) {
                return key.equals(barCode)||barCode.equals(ALL_BARCODE);
            }
        });
        return from(filter);
    }

    @Nonnull
    private <T> Observable.Transformer<T, T> from(@Nonnull final Observable retrySource) {
        requireNonNull(retrySource);
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> source) {
                return source.repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> events) {
                        return events.switchMap(new Func1<Void, Observable<?>>() {
                            @Override
                            public Observable<?> call(Void aVoid) {
                                return retrySource;
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * @return data from memory
     */
    private Observable<Parsed> lazyCache(@Nonnull final BarCode barCode) {
        return Observable
                .defer(new Func0<Observable<Parsed>>() {
                    @Override
                    public Observable<Parsed> call() {
                        return cache(barCode);
                    }
                })
                .onErrorResumeNext(new OnErrorResumeWithEmpty<Parsed>());
    }

    private Observable<Parsed> cache(@Nonnull final BarCode barCode) {
        try {
            return memCache.get(barCode, new Callable<Observable<Parsed>>() {
                @Nonnull
                @Override
                @SuppressWarnings("PMD.SignatureDeclareThrowsException")
                public Observable<Parsed> call() throws Exception {
                    return disk(barCode);
                }
            });
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }


    @Override
    public Observable<Parsed> memory(@Nonnull BarCode barCode) {
        Observable<Parsed> cachedValue = memCache.getIfPresent(barCode);
        return cachedValue == null ? Observable.<Parsed>empty() : cachedValue;
    }

    /**
     * Fetch data from persister and update memory after. If an error occurs, emit and empty observable
     * so that the concat call in {@link #get(BarCode)} moves on to {@link #fetch(BarCode)}
     *
     * @param barCode
     * @return
     */
    @Override
    public Observable<Parsed> disk(@Nonnull final BarCode barCode) {
        return Observable.defer(new Func0<Observable<Parsed>>() {
            @Override
            public Observable<Parsed> call() {
                return persister().read(barCode)
                        .onErrorResumeNext(new OnErrorResumeWithEmpty<Raw>())
                        .map(parser)
                        .doOnNext(new Action1<Parsed>() {
                            @Override
                            public void call(Parsed parsed) {
                                updateMemory(barCode, parsed);
                            }
                        }).cache();
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
    public Observable<Parsed> fetch(@Nonnull final BarCode barCode) {
        return Observable.defer(new Func0<Observable<Parsed>>() {
            @Nullable
            @Override
            public Observable<Parsed> call() {
                return fetchAndPersist(barCode);
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
     * @param barCode resource identifier
     * @return observable that emits a {@link Parsed} value
     */
    @Nullable
    Observable<Parsed> fetchAndPersist(@Nonnull final BarCode barCode) {
        try {
            return inFlightRequests.get(barCode, new Callable<Observable<Parsed>>() {
                @Nonnull
                @Override
                public Observable<Parsed> call() {
                    return response(barCode);
                }
            });
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }

    @Nonnull
    Observable<Parsed> response(@Nonnull final BarCode barCode) {
        return fetcher()
                .fetch(barCode)
                .flatMap(new Func1<Raw, Observable<Parsed>>() {
                    @Override
                    public Observable<Parsed> call(Raw raw) {
                        return persister().write(barCode, raw)
                                .flatMap(new Func1<Boolean, Observable<Parsed>>() {
                                    @Nonnull
                                    @Override
                                    public Observable<Parsed> call(Boolean aBoolean) {
                                        return disk(barCode);
                                    }
                                });
                    }
                })
                .doOnNext(new Action1<Parsed>() {
                    @Override
                    public void call(Parsed data) {
                        notifySubscribers(data);
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
    public Observable<Parsed> stream(@Nonnull BarCode id) {

        Observable<Parsed> stream = subject.asObservable();

        //If nothing was emitted through the subject yet, start stream with get() value
        if (!subject.hasValue()) {
            return stream.startWith(get(id));
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
     * @param barCode
     * @param data
     */
    void updateMemory(@Nonnull final BarCode barCode, final Parsed data) {
        memCache.put(barCode, Observable.just(data));
    }

    @Override
    public void clearMemory() {
        inFlightRequests.invalidateAll();
        clearDiskIfNoOp();
        memCache.invalidateAll();
        refreshSubject.onNext(ALL_BARCODE);
    }

    private void clearDiskIfNoOp() {
        if (persister() instanceof NoopPersister) {
            persister = new NoopPersister<>();
        }
    }

    /**
     * Clear memory by id
     *
     * @param barCode of data to clear
     */
    @Override
    public void clearMemory(@Nonnull final BarCode barCode) {
        inFlightRequests.invalidate(barCode);
        clearDiskIfNoOp();
        memCache.invalidate(barCode);
        refreshSubject.onNext(barCode);
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
    Persister<Raw> persister() {
        return persister;
    }

    /**
     *
     */
    Fetcher<Raw> fetcher() {
        return fetcher;
    }
}

