package com.nytimes.android.external.store.util;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store.base.Clearable;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.MemoryPolicy;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * Pass-through diskdao for stores that don't want to use persister
 */
public class NoopPersister<Raw, Key> implements Persister<Raw, Key>, Clearable<Key> {
    protected final Cache<Key, Observable<Raw>> networkResponses;

    public static <Raw, Key> NoopPersister<Raw, Key> create(MemoryPolicy memoryPolicy) {
        return new NoopPersister<>(memoryPolicy);
    }

    public static <Raw, Key> NoopPersister<Raw, Key> create() {
        MemoryPolicy defaultMemoryPolicy = MemoryPolicy
            .MemoryPolicyBuilder
            .newBuilder()
            .setExpireAfter(TimeUnit.HOURS.toSeconds(24))
            .setExpireAfterTimeUnit(TimeUnit.SECONDS)
            .build();

        return new NoopPersister<>(defaultMemoryPolicy);
    }

    NoopPersister(MemoryPolicy memoryPolicy) {
        this.networkResponses = CacheBuilder
            .newBuilder()
            .expireAfterWrite(memoryPolicy.getExpireAfter(), memoryPolicy.getExpireAfterTimeUnit())
            .build();
    }

    @Nonnull
    @Override
    public Observable<Raw> read(@Nonnull Key key) {
        Observable<Raw> cachedValue = networkResponses.getIfPresent(key);
        return cachedValue == null ? Observable.<Raw>empty() : cachedValue;
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(@Nonnull Key key, @Nonnull Raw raw) {
        networkResponses.put(key, Observable.<Raw>just(raw));
        return Observable.just(true);
    }

    @Override
    public void clear(@Nonnull Key key) {
        networkResponses.invalidate(key);
    }
}
