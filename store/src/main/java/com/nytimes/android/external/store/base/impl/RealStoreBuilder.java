package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.beta.Store;
import com.nytimes.android.external.store.util.NoopParserFunc;
import com.nytimes.android.external.store.util.NoopPersister;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * Builder where there parser is used.
 */
public class RealStoreBuilder<Raw, Parsed, Key> {
    private final List<Parser> parsers = new ArrayList<>();
    private Persister<Raw, Key> persister;
    private Cache<Key, Observable<Parsed>> memCache;
    private Fetcher<Raw, Key> fetcher;

    @Nonnull
    public static <Raw, Parsed, Key> RealStoreBuilder<Raw, Parsed, Key> builder() {
        return new RealStoreBuilder<>();
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> fetcher(final @Nonnull Fetcher<Raw, Key> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> persister(final @Nonnull Persister<Raw, Key> persister) {
        this.persister = persister;
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> persister(final @Nonnull DiskRead<Raw, Key> diskRead,
                                                        final @Nonnull DiskWrite<Raw, Key> diskWrite) {
        persister = new Persister<Raw, Key>() {
            @Nonnull
            @Override
            public Observable<Raw> read(Key barCode) {
                return diskRead.read(barCode);
            }

            @Nonnull
            @Override
            public Observable<Boolean> write(Key barCode, Raw raw) {
                return diskWrite.write(barCode, raw);
            }
        };
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> parser(final @Nonnull Parser<Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add(parser);
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> parsers(final @Nonnull List<Parser> parsers) {
        this.parsers.clear();
        this.parsers.addAll(parsers);
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> memory(Cache<Key, Observable<Parsed>> memCache) {
        this.memCache = memCache;
        return this;
    }

    @Nonnull
    public Store<Parsed, Key> open() {
        if (persister == null) {
            persister = new NoopPersister<>();
        }

        if (parsers.isEmpty()) {
            parser(new NoopParserFunc<Raw, Parsed>());
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
