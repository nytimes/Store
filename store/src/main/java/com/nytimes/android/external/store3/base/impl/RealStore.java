package com.nytimes.android.external.store3.base.impl;

import com.nytimes.android.external.store.util.Result;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.InternalStore;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.util.KeyParser;
import com.nytimes.android.external.store3.util.NoKeyParser;
import com.nytimes.android.external.store3.util.NoopParserFunc;
import com.nytimes.android.external.store3.util.NoopPersister;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class RealStore<Parsed, Key> implements Store<Parsed, Key> {

    private final InternalStore<Parsed, Key> internalStore;

    public RealStore(InternalStore<Parsed, Key> internalStore) {
        this.internalStore = internalStore;
    }

    public RealStore(Fetcher<Parsed, Key> fetcher) {
        final Parser<Parsed, Parsed> noOpFunc = new NoopParserFunc<>();
        internalStore = new RealInternalStore<>(fetcher, NoopPersister.<Parsed, Key>create(),
            new NoKeyParser<Key, Parsed, Parsed>(noOpFunc), StalePolicy.UNSPECIFIED);
    }

    public RealStore(Fetcher<Parsed, Key> fetcher,
                     Persister<Parsed, Key> persister) {
        final Parser<Parsed, Parsed> noOpFunc = new NoopParserFunc<>();
        internalStore = new RealInternalStore<>(fetcher,
            persister,
            new NoKeyParser<Key, Parsed, Parsed>(noOpFunc),
            StalePolicy.UNSPECIFIED);
    }

    public <Raw> RealStore(Fetcher<Raw, Key> fetcher,
                           Persister<Raw, Key> persister,
                           final Parser<Raw, Parsed> parser) {
        internalStore = new RealInternalStore<>(fetcher,
            persister,
            new NoKeyParser<Key, Raw, Parsed>(parser),
            StalePolicy.UNSPECIFIED);
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
    public Single<Parsed> get(@Nonnull final Key key) {
        return internalStore.get(key);
    }

    @Nonnull
    @Override
    public Single<Result<Parsed>> getWithResult(@Nonnull Key key) {
        return internalStore.getWithResult(key);
    }

    @Override
    public Observable<Parsed> getRefreshing(@Nonnull Key key) {
        return internalStore.getRefreshing(key);
    }


    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to network
     *
     * @return data from fetch and store it in memory and persister
     */
    @Nonnull
    @Override
    public Single<Parsed> fetch(@Nonnull final Key key) {
        return internalStore.fetch(key);
    }

    @Nonnull
    @Override
    public Single<Result<Parsed>> fetchWithResult(@Nonnull Key key) {
        return internalStore.fetchWithResult(key);
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

    @Override
    public boolean hasKey(@Nonnull Key key) {
        return internalStore.hasKey(key);
    }

    protected Maybe<Parsed> memory(@Nonnull Key key) {
        return internalStore.memory(key);
    }

    @Nonnull
    protected Maybe<Parsed> disk(@Nonnull Key key) {
        return internalStore.disk(key);
    }

}
