package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.util.KeyParser;
import com.nytimes.android.external.store.util.NoKeyParser;
import com.nytimes.android.external.store.util.NoopParserFunc;
import com.nytimes.android.external.store.util.NoopPersister;


import javax.annotation.Nonnull;

import rx.Observable;

import static com.nytimes.android.external.store.base.impl.StalePolicy.UNSPECIFIED;

public class RealStore<Parsed, Key> implements Store<Parsed, Key> {

    private final InternalStore<Parsed, Key> internalStore;

    public RealStore(InternalStore<Parsed, Key> internalStore) {
        this.internalStore = internalStore;
    }

    public RealStore(Fetcher<Parsed, Key> fetcher) {
        final Parser<Parsed, Parsed> noOpFunc = new NoopParserFunc<>();
        internalStore = new RealInternalStore<>(fetcher, NoopPersister.<Parsed, Key>create(),
            new NoKeyParser<Key, Parsed, Parsed>(noOpFunc), UNSPECIFIED);
    }

    public RealStore(Fetcher<Parsed, Key> fetcher,
                     Persister<Parsed, Key> persister) {
        final Parser<Parsed, Parsed> noOpFunc = new NoopParserFunc<>();
        internalStore = new RealInternalStore<>(fetcher,
            persister,
            new NoKeyParser<Key, Parsed, Parsed>(noOpFunc),
            UNSPECIFIED);
    }

    public <Raw> RealStore(Fetcher<Raw, Key> fetcher,
                           Persister<Raw, Key> persister,
                           final Parser<Raw, Parsed> parser) {
        internalStore = new RealInternalStore<>(fetcher,
            persister,
            new NoKeyParser<Key, Raw, Parsed>(parser),
            UNSPECIFIED);
    }


    public <Raw> RealStore(Fetcher<Raw, Key> fetcher,
                           Persister<Raw, Key> persister,
                           Parser<Raw, Parsed> parser,
                           MemoryPolicy memoryPolicy,
                           StalePolicy policy) {
        internalStore = new RealInternalStore<>(fetcher, persister,
            new NoKeyParser<Key, Raw, Parsed>(parser), memoryPolicy, policy);
    }

    public <Raw> RealStore(Fetcher<Raw, Key> fetcher,
                           Persister<Raw, Key> persister,
                           KeyParser<Key, Raw, Parsed> parser,
                           MemoryPolicy memoryPolicy,
                           StalePolicy policy) {
        internalStore = new RealInternalStore<>(fetcher, persister,
            parser, memoryPolicy, policy);
    }


    @Nonnull
    @Override
    public Observable<Parsed> get(@Nonnull final Key key) {
        return internalStore.get(key);
    }

    @Override
    public Observable<Parsed> getRefreshing(@Nonnull Key key) {
        return internalStore.getRefreshing(key);
    }


    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to nerwork
     *
     * @return data from fetch and store it in memory and persister
     */
    @Nonnull
    @Override
    public Observable<Parsed> fetch(@Nonnull final Key key) {
        return internalStore.fetch(key);
    }

    @Nonnull
    @Override
    public Observable<Parsed> stream() {
        return internalStore.stream();
    }

    @Nonnull
    @Override
    public Observable<Parsed> stream(Key key) {
        return internalStore.stream(key);
    }

    @Override
    public void clearMemory() {
        internalStore.clearMemory();
    }

    /**
     * Clear memory by id
     *
     * @param key of data to clear
     */
    @Override
    public void clearMemory(@Nonnull final Key key) {
        internalStore.clearMemory(key);
    }

    @Override
    public void clear() {
        internalStore.clear();
    }

    @Override
    public void clear(@Nonnull Key key) {
        internalStore.clear(key);
    }

    protected Observable<Parsed> memory(@Nonnull Key key) {
        return internalStore.memory(key);
    }

    @Nonnull
    protected Observable<Parsed> disk(@Nonnull Key key) {
        return internalStore.disk(key);
    }

}
