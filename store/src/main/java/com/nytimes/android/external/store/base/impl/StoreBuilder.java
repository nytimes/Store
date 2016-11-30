package com.nytimes.android.external.store.base.impl;

import android.support.annotation.NonNull;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.util.NoopParserFunc;
import com.nytimes.android.external.store.util.NoopPersister;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Func1;

/**
 * Builder where there parser is used.
 */
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public class StoreBuilder<T> {
    private Fetcher<T> fetcher;
    private Persister<T> persister;
    private Cache<BarCode, Observable<T>> memCache;

    public static <Raw> StoreBuilder<Raw> builder() {
        return new StoreBuilder<>();
    }

    public StoreBuilder<T> fetcher(final @NonNull Fetcher<T> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public StoreBuilder<T> nonObservableFetcher(final @NonNull Func1<BarCode, T> fetcher) {
        this.fetcher = new Fetcher<T>() {
            @Override
            public Observable<T> fetch(final BarCode barCode) {
                return Observable.fromCallable(new Callable<T>() {
                    @SuppressWarnings("all")
                    @Override
                    public T call() throws Exception {
                        return fetcher.call(barCode);
                    }
                });
            }
        };
        return this;
    }

    public StoreBuilder<T> persister(final @NonNull Persister<T> persister) {
        this.persister = persister;
        return this;
    }

    public StoreBuilder<T> persister(final @NonNull DiskRead<T> diskRead,
                                     final @NonNull DiskWrite<T> diskWrite) {
        persister = new Persister<T>() {
            @Override
            public Observable<T> read(BarCode barCode) {
                return diskRead.read(barCode);
            }

            @Override
            public Observable<Boolean> write(BarCode barCode, T t) {
                return diskWrite.write(barCode, t);
            }
        };
        return this;
    }

    public StoreBuilder<T> memory(Cache<BarCode, Observable<T>> memCache) {
        this.memCache = memCache;
        return this;
    }

    public Store<T> open() {
        if (persister == null) {
            persister = new NoopPersister<>();
        }
        InternalStore<T> internalStore;

        if (memCache == null) {
            internalStore = new RealInternalStore<>(fetcher, persister, new NoopParserFunc<T, T>());
        } else {
            internalStore = new RealInternalStore<>(fetcher, persister, new NoopParserFunc<T, T>(), memCache);
        }
        return new RealStore<>(internalStore);

    }
}
