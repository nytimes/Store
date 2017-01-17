package com.nytimes.android.external.store.base.impl;

import android.support.annotation.NonNull;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.BarCode;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.util.NoopPersister;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Func1;

/**
 * Builder where there parser is used.
 */
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public class ParsingStoreBuilder<Raw, Parsed> {

    private final List<Parser> parsers = new ArrayList<>();
    private Fetcher<Raw> fetcher;
    private Persister<Raw> persister;
    private Cache<BarCode, Observable<Parsed>> memCache;

    public ParsingStoreBuilder() {

    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> fetcher(final @NonNull Fetcher<Raw> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> nonObservableFetcher(
            final @NonNull Func1<BarCode, Raw> fetcher) {
        this.fetcher = new Fetcher<Raw>() {
            @NonNull
            @Override
            public Observable<Raw> fetch(
                    final BarCode barCode) {
                return Observable.fromCallable(new Callable<Raw>() {
                    @Override
                    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
                    public Raw call() throws Exception {
                        return fetcher.call(barCode);
                    }
                });
            }
        };
        return this;
    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> persister(final @NonNull Persister<Raw> persister) {
        this.persister = persister;
        return this;
    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> persister(final @NonNull DiskRead<Raw> diskRead,
                                                      final @NonNull DiskWrite<Raw> diskWrite) {
        persister = new Persister<Raw>() {
            @NonNull
            @Override
            public Observable<Raw> read(BarCode barCode) {
                return diskRead.read(barCode);
            }

            @NonNull
            @Override
            public Observable<Boolean> write(
                    BarCode barCode, Raw raw) {
                return diskWrite.write(barCode, raw);
            }
        };
        return this;
    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> parser(final @NonNull Func1<Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add((Parser<Raw, Parsed>) parser);
        return this;
    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> parser(final @NonNull Parser<Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add(parser);
        return this;
    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> parsers(final @NonNull List<Parser> parsers) {
        this.parsers.clear();
        this.parsers.addAll(parsers);
        return this;
    }

    @NonNull
    public static <Raw, Parsed> ParsingStoreBuilder<Raw, Parsed> builder() {
        return new ParsingStoreBuilder<>();
    }

    @NonNull
    public ParsingStoreBuilder<Raw, Parsed> memory(
            Cache<BarCode, Observable<Parsed>> memCache) {
        this.memCache = memCache;
        return this;
    }

    @NonNull
    public Store<Parsed> open() {
        if (persister == null) {
            persister = new NoopPersister<>();
        }

        Parser<Raw, Parsed> multiParser = new MultiParser<>(parsers);
        RealInternalStore<Raw, Parsed> realInternalStore;

        if (memCache == null) {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser);
        } else {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser, memCache);
        }

        return new RealStore<>(realInternalStore);
    }
}
