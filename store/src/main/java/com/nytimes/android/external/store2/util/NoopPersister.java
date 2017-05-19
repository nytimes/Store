package com.nytimes.android.external.store2.util;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store2.base.Clearable;
import com.nytimes.android.external.store2.base.Persister;
import com.nytimes.android.external.store2.base.impl.MemoryPolicy;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Single;


/**
 * Pass-through diskdao for stores that don't want to use persister
 */
public class NoopPersister<Raw, Key> implements Persister<Raw, Key>, Clearable<Key> {
    protected final Cache<Key, Maybe<Raw>> networkResponses;

    NoopPersister(MemoryPolicy memoryPolicy) {
        this.networkResponses = CacheBuilder
            .newBuilder()
            .expireAfterWrite(memoryPolicy.getExpireAfterWrite(), memoryPolicy.getExpireAfterTimeUnit())
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
                .setExpireAfterWrite(TimeUnit.HOURS.toSeconds(24))
                .setExpireAfterTimeUnit(TimeUnit.SECONDS)
                .build();
        } else {
            memPolicy = memoryPolicy;
        }

        return new NoopPersister<>(memPolicy);
    }

    @Nonnull
    @Override
    public Maybe<Raw> read(@Nonnull Key key) {
        Maybe<Raw> cachedValue = networkResponses.getIfPresent(key);
        return cachedValue == null ? Maybe.<Raw>empty() : cachedValue;
    }

    @Nonnull
    @Override
    public Single<Boolean> write(@Nonnull Key key, @Nonnull Raw raw) {
        networkResponses.put(key, Maybe.just(raw));
        return Single.just(true);
    }

    @Override
    public void clear(@Nonnull Key key) {
        networkResponses.invalidate(key);
    }
}
