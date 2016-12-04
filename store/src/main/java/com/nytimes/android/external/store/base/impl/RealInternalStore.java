package com.nytimes.android.external.store.base.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.util.OnErrorResumeWithEmpty;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

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


    public static final String TAG = RealInternalStore.class.getSimpleName();
    Cache<BarCode, Observable<Parsed>> inFlightRequests;

    Cache<BarCode, Observable<Parsed>> memCache;

    private Fetcher<Raw> fetcher;
    private Persister<Raw> persister;
    private Func1<Raw, Parsed> parser;
    private BehaviorSubject<Parsed> subject;

    public RealInternalStore(Fetcher<Raw> fetcher,
                             Persister<Raw> persister,
                             Func1<Raw, Parsed> parser) {
        memCache = CacheBuilder.newBuilder()
                .maximumSize(getCacheSize())
                .expireAfterAccess(getCacheTTL(), TimeUnit.SECONDS)
                .build();
        init(fetcher, persister, parser, memCache);
    }


    public RealInternalStore(Fetcher<Raw> fetcher,
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
    public Observable<Parsed> get(@NonNull final BarCode barCode) {
        return Observable.concat(
                cache(barCode),
                fetch(barCode)
        ).take(1);
    }

    /**
     * @return data from memory
     */
    private Observable<Parsed> cache(@NonNull final BarCode barCode) {
        try {
            return memCache.get(barCode, new Callable<Observable<Parsed>>() {
                @Override
                public Observable<Parsed> call() throws Exception {
                    return disk(barCode);
                }
            })
                    .onErrorResumeNext(new OnErrorResumeWithEmpty<Parsed>());
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }


    @Override
    public Observable<Parsed> memory(@NonNull BarCode barCode) {
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
    public Observable<Parsed> disk(@NonNull final BarCode barCode) {
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

    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to nerwork
     *
     * @return data from fetch and store it in memory and persister
     */
    public Observable<Parsed> fetch(@NonNull final BarCode barCode) {
        return Observable.defer(new Func0<Observable<Parsed>>() {
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
    Observable<Parsed> fetchAndPersist(@NonNull final BarCode barCode) {
        try {
            return inFlightRequests.get(barCode, new Callable<Observable<Parsed>>() {
                @Override
                public Observable<Parsed> call() {
                    return response(barCode);
                }
            });
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }

    @NonNull
    Observable<Parsed> response(@NonNull final BarCode barCode) {
        return fetcher()
                .fetch(barCode)
                .flatMap(new Func1<Raw, Observable<Parsed>>() {
                    @Override
                    public Observable<Parsed> call(Raw raw) {
                        //Log.i(TAG,"writing and then reading from Persister");
                        return persister().write(barCode, raw)
                                .flatMap(new Func1<Boolean, Observable<Parsed>>() {
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
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        inFlightRequests.invalidate(barCode);
                    }
                });
    }

    void notifySubscribers(Parsed data) {
        //Log.d(TAG,"notify stream subscribers of fresh data");
        subject.onNext(data);
    }

    /**
     * Get data stream for Subjects with the argument id
     *
     * @return
     */
    public Observable<Parsed> stream(BarCode id) {

        Observable<Parsed> stream = subject.asObservable();

        //If nothing was emitted through the subject yet, start stream with get() value
        if (!subject.hasValue()) {
            return stream.startWith(get(id));
        }

        return stream;
    }

    /**
     * Only update memory after persister has been successfully update
     *
     * @param barCode
     * @param data
     */
    void updateMemory(@NonNull final BarCode barCode, final Parsed data) {
        memCache.put(barCode, Observable.just(data));
    }

    public void clearMemory() {
        memCache.invalidateAll();
    }

    /**
     * Clear memory by id
     *
     * @param barCode of data to clear
     */
    public void clearMemory(@NonNull final BarCode barCode) {
        memCache.invalidate(barCode);
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
//        return memCache.size();
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

