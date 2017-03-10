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

    NoopPersister(MemoryPolicy memoryPolicy) {
        this.networkResponses = CacheBuilder
            .newBuilder()
            .expireAfterWrite(memoryPolicy.getExpireAfter(), memoryPolicy.getExpireAfterTimeUnit())
            .build();
    }

    public static <Raw, Key> NoopPersister<Raw, Key> create() {
        return NoopPersister.create(null);
    }

    public static <Raw, Key> NoopPersister<Raw, Key> create(MemoryPolicy memoryPolicy) {
        //For some reason PMD requires a local variable instead of modifying the passed one.
        MemoryPolicy memPolicy;

        if (memoryPolicy == null) {
            memPolicy = MemoryPolicy
                .builder()
                .setExpireAfter(TimeUnit.HOURS.toSeconds(24))
                .setExpireAfterTimeUnit(TimeUnit.SECONDS)
                .build();
        } else {
            memPolicy = memoryPolicy;
        }

        return new NoopPersister<>(memPolicy);
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
