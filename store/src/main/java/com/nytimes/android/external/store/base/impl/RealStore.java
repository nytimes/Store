package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.InternalStore;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.util.NoopParserFunc;
import com.nytimes.android.external.store.util.NoopPersister;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.functions.Func1;

import static com.nytimes.android.external.store.base.impl.StalePolicy.UNSPECIFIED;

public class RealStore<Parsed, Key> implements Store<Parsed, Key> {

    private final InternalStore<Parsed, Key> internalStore;

    RealStore(InternalStore<Parsed, Key> internalStore) {
        this.internalStore = internalStore;
    }

    public RealStore(Fetcher<Parsed, Key> fetcher) {
        internalStore = new RealInternalStore<>(fetcher, new NoopPersister<Parsed, Key>(),
                new NoopParserFunc<Parsed, Parsed>(), UNSPECIFIED);
    }

    public RealStore(Fetcher<Parsed, Key> fetcher, Persister<Parsed, Key> persister) {
        internalStore = new RealInternalStore<>(fetcher, persister,
                new NoopParserFunc<Parsed, Parsed>(), UNSPECIFIED);
    }

    public <Raw> RealStore(Fetcher<Raw, Key> fetcher,
                           Persister<Raw, Key> persister,
                           Parser<Raw, Parsed> parser) {
        internalStore = new RealInternalStore<>(fetcher, persister, parser, UNSPECIFIED);
    }


    public <Raw> RealStore(Fetcher<Raw, Key> fetcher,
                           Persister<Raw, Key> persister,
                           Func1<Raw, Parsed> parser, Cache<Key, Observable<Parsed>> memCache) {
        internalStore = new RealInternalStore<>(fetcher, persister, parser, memCache, UNSPECIFIED);
    }


    public <Raw> RealStore(Fetcher<Raw, Key> fetcher,
                           Persister<Raw, Key> persister,
                           Cache<Key, Observable<Parsed>> memCache) {
        internalStore = new RealInternalStore<>(fetcher, persister,
                new NoopParserFunc<Raw, Parsed>(), memCache, UNSPECIFIED);
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
        internalStore.clear();
    }

    protected Observable<Parsed> memory(@Nonnull Key key) {
        return internalStore.memory(key);
    }

    @Nonnull
    protected Observable<Parsed> disk(@Nonnull Key key) {
        return internalStore.disk(key);
    }

}
