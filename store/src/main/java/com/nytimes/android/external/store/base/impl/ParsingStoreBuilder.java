package com.nytimes.android.external.store.base.impl;

import android.support.annotation.NonNull;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.util.NoopPersister;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Func1;

/**
 * Builder where there parser is used.
 */
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public class ParsingStoreBuilder<Raw, Parsed> {
    private Fetcher<Raw> fetcher;
    private Persister<Raw> persister;
    private Func1<Raw, Parsed> parser;
    private Cache<BarCode, Observable<Parsed>> memCache;

    public ParsingStoreBuilder() {

    }

    public ParsingStoreBuilder<Raw, Parsed> fetcher(final @NonNull Fetcher<Raw> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public ParsingStoreBuilder<Raw, Parsed> nonObservableFetcher(final @NonNull Func1<BarCode, Raw> fetcher) {
        this.fetcher = new Fetcher<Raw>() {
            @Override
            public Observable<Raw> fetch(final BarCode barCode) {
                return Observable.fromCallable(new Callable<Raw>() {
                    @Override
                    public Raw call() throws Exception {
                        return fetcher.call(barCode);
                    }
                });
            }
        };
        return this;
    }

    public ParsingStoreBuilder<Raw, Parsed> persister(final @NonNull Persister<Raw> persister) {
        this.persister = persister;
        return this;
    }

    public ParsingStoreBuilder<Raw, Parsed> persister(final @NonNull DiskRead<Raw> diskRead,
                                                      final @NonNull DiskWrite<Raw> diskWrite) {
        persister = new Persister<Raw>() {
            @Override
            public Observable<Raw> read(BarCode barCode) {
                return diskRead.read(barCode);
            }

            @Override
            public Observable<Boolean> write(BarCode barCode, Raw raw) {
                return diskWrite.write(barCode, raw);
            }
        };
        return this;
    }

    public ParsingStoreBuilder<Raw, Parsed> parser(final @NonNull Func1<Raw, Parsed> parser) {
        this.parser = parser;
        return this;
    }

    public ParsingStoreBuilder<Raw, Parsed> parser(final @NonNull Parser<Raw, Parsed> parser) {
        this.parser = parser;
        return this;
    }

    public static <Raw, Parsed> ParsingStoreBuilder<Raw, Parsed> builder() {
        return new ParsingStoreBuilder<>();
    }

    public ParsingStoreBuilder<Raw, Parsed> memory(Cache<BarCode, Observable<Parsed>> memCache) {
        this.memCache = memCache;
        return this;
    }

    public Store<Parsed> open() {
        if (persister == null) {
            persister = new NoopPersister<>();
        }
        if (parser == null) {
            throw new IllegalArgumentException("Parser cannot be null");
        }
        RealInternalStore<Raw, Parsed> realInternalStore;
        if (memCache == null) {
            realInternalStore = new RealInternalStore<>(fetcher, persister, parser);
        } else {
            realInternalStore = new RealInternalStore<>(fetcher, persister, parser, memCache);
        }
        return new RealStore<>(realInternalStore);
    }
}
