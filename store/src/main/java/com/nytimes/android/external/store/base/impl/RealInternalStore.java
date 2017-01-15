package com.nytimes.android.external.store.base.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.IBarCode;
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

    Cache<IBarCode, Observable<Parsed>> inFlightRequests;
    Cache<IBarCode, Observable<Parsed>> memCache;

    private Fetcher<Raw> fetcher;
    private Persister<Raw> persister;
    private Func1<Raw, Parsed> parser;
    private BehaviorSubject<Parsed> subject;

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
                      Cache<IBarCode, Observable<Parsed>> memCache) {
        init(fetcher, persister, parser, memCache);
    }

    private void init(Fetcher<Raw> fetcher,
                      Persister<Raw> persister,
                      Func1<Raw, Parsed> parser,
                      Cache<IBarCode, Observable<Parsed>> memCache) {
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
     * @param IBarCode
     * @return an observable from the first data source that is available
     */
    public Observable<Parsed> get(@NonNull final IBarCode IBarCode) {
        return Observable.concat(
                cache(IBarCode),
                fetch(IBarCode)
        ).take(1);
    }

    /**
     * @return data from memory
     */
    private Observable<Parsed> cache(@NonNull final IBarCode IBarCode) {
        try {
            return memCache.get(IBarCode, new Callable<Observable<Parsed>>() {
                @NonNull
                @Override
                @SuppressWarnings("PMD.SignatureDeclareThrowsException")
                public Observable<Parsed> call() throws Exception {
                    return disk(IBarCode);
                }
            })
                    .onErrorResumeNext(new OnErrorResumeWithEmpty<Parsed>());
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }


    @Override
    public Observable<Parsed> memory(@NonNull IBarCode IBarCode) {
        Observable<Parsed> cachedValue = memCache.getIfPresent(IBarCode);
        return cachedValue == null ? Observable.<Parsed>empty() : cachedValue;
    }

    /**
     * Fetch data from persister and update memory after. If an error occurs, emit and empty observable
     * so that the concat call in {@link #get(IBarCode)} moves on to {@link #fetch(IBarCode)}
     *
     * @param IBarCode
     * @return
     */
    public Observable<Parsed> disk(@NonNull final IBarCode IBarCode) {
        return persister().read(IBarCode)
                .onErrorResumeNext(new OnErrorResumeWithEmpty<Raw>())
                .map(parser)
                .doOnNext(new Action1<Parsed>() {
                    @Override
                    public void call(Parsed parsed) {
                        updateMemory(IBarCode, parsed);
                    }
                }).cache();
    }

    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to nerwork
     *
     * @return data from fetch and store it in memory and persister
     */
    public Observable<Parsed> fetch(@NonNull final IBarCode IBarCode) {
        return Observable.defer(new Func0<Observable<Parsed>>() {
            @Nullable
            @Override
            public Observable<Parsed> call() {
                return fetchAndPersist(IBarCode);
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
     * @param IBarCode resource identifier
     * @return observable that emits a {@link Parsed} value
     */
    @Nullable
    Observable<Parsed> fetchAndPersist(@NonNull final IBarCode IBarCode) {
        try {
            return inFlightRequests.get(IBarCode, new Callable<Observable<Parsed>>() {
                @NonNull
                @Override
                public Observable<Parsed> call() {
                    return response(IBarCode);
                }
            });
        } catch (ExecutionException e) {
            return Observable.empty();
        }
    }

    @NonNull
    Observable<Parsed> response(@NonNull final IBarCode IBarCode) {
        return fetcher()
                .fetch(IBarCode)
                .flatMap(new Func1<Raw, Observable<Parsed>>() {
                    @Override
                    public Observable<Parsed> call(Raw raw) {
                        //Log.i(TAG,"writing and then reading from Persister");
                        return persister().write(IBarCode, raw)
                                .flatMap(new Func1<Boolean, Observable<Parsed>>() {
                                    @NonNull
                                    @Override
                                    public Observable<Parsed> call(Boolean aBoolean) {
                                        return disk(IBarCode);
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
                        inFlightRequests.invalidate(IBarCode);
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
    public Observable<Parsed> stream(@NonNull IBarCode id) {

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
     * @param IBarCode
     * @param data
     */
    void updateMemory(@NonNull final IBarCode IBarCode, final Parsed data) {
        memCache.put(IBarCode, Observable.just(data));
    }

    public void clearMemory() {
        memCache.invalidateAll();
    }

    /**
     * Clear memory by id
     *
     * @param IBarCode of data to clear
     */
    public void clearMemory(@NonNull final IBarCode IBarCode) {
        memCache.invalidate(IBarCode);
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

