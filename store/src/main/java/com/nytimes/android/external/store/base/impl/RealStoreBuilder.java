package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.util.KeyParser;
import com.nytimes.android.external.store.util.NoKeyParser;
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
    private final List<KeyParser> parsers = new ArrayList<>();
    private Persister<Raw, Key> persister;
    private Cache<Key, Observable<Parsed>> memCache;
    private Fetcher<Raw, Key> fetcher;

    @SuppressWarnings("PMD.UnusedPrivateField") //remove when it is implemented...
    private StalePolicy stalePolicy = StalePolicy.UNSPECIFIED;

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
            public Observable<Raw> read(@Nonnull Key key) {
                return diskRead.read(key);
            }

            @Nonnull
            @Override
            public Observable<Boolean> write(@Nonnull Key key, @Nonnull Raw raw) {
                return diskWrite.write(key, raw);
            }
        };
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> parser(final @Nonnull Parser<Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add(new NoKeyParser<>(parser));
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> parser(final @Nonnull KeyParser<Key, Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add(parser);
        return this;
    }


    @Nonnull
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public RealStoreBuilder<Raw, Parsed, Key> parsers(final @Nonnull List<Parser> parsers) {
        this.parsers.clear();
        for (Parser parser : parsers) {
            this.parsers.add(new NoKeyParser<>(parser));
        }
        return this;
    }

    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> memory(Cache<Key, Observable<Parsed>> memCache) {
        this.memCache = memCache;
        return this;
    }

    //Store will backfill the disk cache anytime a record is stale
    //User will still get the stale record returned to them
    public RealStoreBuilder<Raw, Parsed, Key> refreshOnStale() {
        stalePolicy = StalePolicy.REFRESH_ON_STALE;
        return this;
    }

    //Store will try to get network source when disk data is stale
    //if network source throws error or is empty, stale disk data will be returned
    @Nonnull
    public RealStoreBuilder<Raw, Parsed, Key> networkBeforeStale() {
        stalePolicy = StalePolicy.NETWORK_BEFORE_STALE;
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

        KeyParser<Key, Raw, Parsed> multiParser = new MultiParser<>(parsers);
        RealInternalStore<Raw, Parsed, Key> realInternalStore;

        if (memCache == null) {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser, stalePolicy);
        } else {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser, memCache, stalePolicy);
        }

        return new RealStore<>(realInternalStore);
    }
}
