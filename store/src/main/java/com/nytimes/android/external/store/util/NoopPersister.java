package com.nytimes.android.external.store.util;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store.base.Clearable;
import com.nytimes.android.external.store.base.Persister;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * Pass-through diskdao for stores that don't want to use persister
 */
public class NoopPersister<Raw, Key> implements Persister<Raw, Key>, Clearable<Key> {
    protected final Cache<Key, Observable<Raw>> networkResponses;

    public static <Raw, Key> NoopPersister<Raw, Key> create(long expireAfter, TimeUnit expireAfterTimeUnit) {
        return new NoopPersister<>(expireAfter, expireAfterTimeUnit);
    }

    public static <Raw, Key> NoopPersister<Raw, Key> create() {
        return new NoopPersister<>(TimeUnit.HOURS.toSeconds(24), TimeUnit.SECONDS);
    }

    NoopPersister(long expireAfter, TimeUnit expireAfterTimeUnit) {
        this.networkResponses = CacheBuilder
                .newBuilder()
                .expireAfterWrite(expireAfter, expireAfterTimeUnit)
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
