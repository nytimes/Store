package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.beta.Store;
import com.nytimes.android.external.store.util.NoopPersister;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Builder where there parser is used.
 */
public class RealStoreBuilder<Raw, Parsed, Key> {
    private final List<Parser> parsers = new ArrayList<>();
    private Persister<Raw, Key> persister;
    private Cache<Key, Observable<Parsed>> memCache;
    private Fetcher<Raw, Key> fetcher;

    @NotNull
    public static <Raw, Parsed, Key> RealStoreBuilder<Raw, Parsed, Key> builder() {
        return new RealStoreBuilder<>();
    }

    @NotNull
    public RealStoreBuilder<Raw, Parsed, Key> fetcher(final @NotNull Fetcher<Raw, Key> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    @NotNull
    public RealStoreBuilder<Raw, Parsed, Key> persister(final @NotNull Persister<Raw, Key> persister) {
        this.persister = persister;
        return this;
    }

    @NotNull
    public RealStoreBuilder<Raw, Parsed, Key> persister(final @NotNull DiskRead<Raw, Key> diskRead,
                                                        final @NotNull DiskWrite<Raw, Key> diskWrite) {
        persister = new Persister<Raw, Key>() {
            @NotNull
            @Override
            public Observable<Raw> read(Key barCode) {
                return diskRead.read(barCode);
            }

            @NotNull
            @Override
            public Observable<Boolean> write(Key barCode, Raw raw) {
                return diskWrite.write(barCode, raw);
            }
        };
        return this;
    }

    @NotNull
    public RealStoreBuilder<Raw, Parsed, Key> parser(final @NotNull Parser<Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add(parser);
        return this;
    }

    @NotNull
    public RealStoreBuilder<Raw, Parsed, Key> parsers(final @NotNull List<Parser> parsers) {
        this.parsers.clear();
        this.parsers.addAll(parsers);
        return this;
    }

    @NotNull
    public RealStoreBuilder<Raw, Parsed, Key> memory(Cache<Key, Observable<Parsed>> memCache) {
        this.memCache = memCache;
        return this;
    }

    @NotNull
    public Store<Parsed, Key> open() {
        if (persister == null) {
            persister = new NoopPersister<>();
        }

        Parser<Raw, Parsed> multiParser = new MultiParser<>(parsers);
        RealInternalStore<Raw, Parsed, Key> realInternalStore;

        if (memCache == null) {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser);
        } else {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser, memCache);
        }

        return new RealStore<>(realInternalStore);
    }
}
