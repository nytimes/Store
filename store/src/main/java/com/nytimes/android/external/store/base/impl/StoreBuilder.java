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

import javax.annotation.Nonnull;

import rx.Observable;
import rx.annotations.Beta;


/**
 * Builder where there parser is used.
 */
public final class StoreBuilder<Raw> {
    private Fetcher<Raw, BarCode> fetcher;
    private Persister<Raw, BarCode> persister;
    private Cache<BarCode, Observable<Raw>> memCache;

    @SuppressWarnings("PMD.UnusedPrivateField") //remove when it is implemented...
    private StalePolicy stalePolicy = StalePolicy.UNSPECIFIED;

    public enum StalePolicy {
        UNSPECIFIED, REFRESH_ON_STALE, NETWORK_BEFORE_STALE
    }

    @Nonnull
    @Deprecated
    //Please Use fromTypes to build Stores, allowing customization of Barcode Type
    public static <Raw> StoreBuilder<Raw> builder() {
        return new StoreBuilder<>();
    }

    @Beta
    public static <Key, Raw, Parsed> RealStoreBuilder<Raw, Parsed, Key> fromTypes(Class<Key> keyClass,
                                                                                  Class<Raw> rawClass,
                                                                                  Class<Parsed> parsedClass
    ) {
        return new RealStoreBuilder<>();
    }

    @Beta
    public static <Key, Parsed> RealStoreBuilder<Parsed, Parsed, Key> fromTypes(Class<Key> keyClass,
                                                                                Class<Parsed> returnClass) {
        return new RealStoreBuilder<>();
    }

    @Beta
    public static <Parsed> RealStoreBuilder<Parsed, Parsed, BarCode> fromTypes(Class<Parsed> returnClass) {
        return new RealStoreBuilder<>();
    }

    /**
     * Please Use fromTypes to build Stores, allowing customization of Barcode Type
     */
    @Deprecated
    @Nonnull
    public StoreBuilder<Raw> fetcher(final @Nonnull Fetcher<Raw, BarCode> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    /**
     * Please Use fromTypes to build Stores, allowing customization of Barcode Type
     */
    @Deprecated
    @Nonnull
    public StoreBuilder<Raw> persister(final @Nonnull Persister<Raw, BarCode> persister) {
        this.persister = persister;
        return this;
    }

    public StoreBuilder<Raw> refreshOnStale() {
        stalePolicy = StalePolicy.REFRESH_ON_STALE;
        return this;
    }

    @Nonnull
    public StoreBuilder<Raw> networkBeforeStale() {
        stalePolicy = StalePolicy.NETWORK_BEFORE_STALE;
        return this;
    }

    /**
     * Please Use fromTypes to build Stores, allowing customization of Barcode Type
     */
    @Deprecated
    @Nonnull
    public StoreBuilder<Raw> persister(final @Nonnull DiskRead<Raw, BarCode> diskRead,
                                       final @Nonnull DiskWrite<Raw, BarCode> diskWrite) {
        persister = new Persister<Raw, BarCode>() {
            @Nonnull
            @Override
            public Observable<Raw> read(BarCode barCode) {
                return diskRead.read(barCode);
            }

            @Nonnull
            @Override
            public Observable<Boolean> write(BarCode barCode, Raw t) {
                return diskWrite.write(barCode, t);
            }
        };
        return this;
    }

    /**
     * Please Use fromTypes to build Stores, allowing customization of Barcode Type
     */
    @Nonnull
    @Deprecated
    public StoreBuilder<Raw> memory(Cache<BarCode, Observable<Raw>> memCache) {
        this.memCache = memCache;
        return this;
    }

    /**
     * Please Use fromTypes to build Stores, allowing customization of Barcode Type
     */
    @Nonnull
    @Deprecated
    public Store<Raw> open() {
        if (persister == null) {
            persister = new NoopPersister<>();
        }
        InternalStore<Raw, BarCode> internalStore;

        if (memCache == null) {
            internalStore = new RealInternalStore<>(fetcher, persister, new NoopParserFunc<Raw, Raw>());
        } else {
            internalStore = new RealInternalStore<>(fetcher, persister, new NoopParserFunc<Raw, Raw>(), memCache);
        }
        return new ProxyStore<Raw>(internalStore);

    }

}
