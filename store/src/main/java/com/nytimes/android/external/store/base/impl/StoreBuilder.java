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

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

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

    @SuppressWarnings("PMD.UnusedPrivateField") //remove when it is implemented...
    private StalePolicy stalePolicy = StalePolicy.Unspecified;

    public enum StalePolicy {
        Unspecified, refreshOnStale, networkBeforeStale
    }

    @Nonnull
    public static <Raw> StoreBuilder<Raw> builder() {
        return new StoreBuilder<>();
    }

    @Nonnull
    public StoreBuilder<T> fetcher(final @Nonnull Fetcher<T> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    @Nonnull
    public StoreBuilder<T> nonObservableFetcher(final @Nonnull Func1<BarCode, T> fetcher) {
        this.fetcher = new Fetcher<T>() {
            @Nonnull
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

    @Nonnull
    public StoreBuilder<T> refreshOnStale() {
        stalePolicy = StalePolicy.refreshOnStale;
        return this;
    }

    @Nonnull
    public StoreBuilder<T> networkBeforeStale() {
        stalePolicy = StalePolicy.networkBeforeStale;
        return this;
    }

    @Nonnull
    public StoreBuilder<T> persister(final @Nonnull Persister<T> persister) {
        this.persister = persister;
        return this;
    }

    @Nonnull
    public StoreBuilder<T> persister(final @Nonnull DiskRead<T> diskRead,
                                     final @Nonnull DiskWrite<T> diskWrite) {
        persister = new Persister<T>() {
            @Nonnull
            @Override
            public Observable<T> read(BarCode barCode) {
                return diskRead.read(barCode);
            }

            @Nonnull
            @Override
            public Observable<Boolean> write(BarCode barCode, T t) {
                return diskWrite.write(barCode, t);
            }
        };
        return this;
    }

    @Nonnull
    public StoreBuilder<T> memory(Cache<BarCode, Observable<T>> memCache) {
        this.memCache = memCache;
        return this;
    }

    @Nonnull
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
