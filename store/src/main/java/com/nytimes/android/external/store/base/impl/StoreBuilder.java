package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.util.NoopParserFunc;
import com.nytimes.android.external.store.util.NoopPersister;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public static <Raw> StoreBuilder<Raw> builder() {
        return new StoreBuilder<>();
    }

    @NotNull
    public StoreBuilder<T> fetcher(final @NotNull Fetcher<T> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    @NotNull
    public StoreBuilder<T> nonObservableFetcher(final @NotNull Func1<BarCode, T> fetcher) {
        this.fetcher = new Fetcher<T>() {
            @NotNull
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

    @NotNull
    public StoreBuilder<T> persister(final @NotNull Persister<T> persister) {
        this.persister = persister;
        return this;
    }

    @NotNull
    public StoreBuilder<T> persister(final @NotNull DiskRead<T> diskRead,
                                     final @NotNull DiskWrite<T> diskWrite) {
        persister = new Persister<T>() {
            @NotNull
            @Override
            public Observable<T> read(BarCode barCode) {
                return diskRead.read(barCode);
            }

            @NotNull
            @Override
            public Observable<Boolean> write(BarCode barCode, T t) {
                return diskWrite.write(barCode, t);
            }
        };
        return this;
    }

    @NotNull
    public StoreBuilder<T> memory(Cache<BarCode, Observable<T>> memCache) {
        this.memCache = memCache;
        return this;
    }

    @NotNull
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
