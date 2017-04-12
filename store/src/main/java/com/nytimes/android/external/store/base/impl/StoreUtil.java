package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.cache.CacheBuilder;
import com.nytimes.android.external.store.base.Clearable;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.RecordProvider;
import com.nytimes.android.external.store.base.RecordState;

import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import static com.nytimes.android.external.store.base.RecordState.STALE;

final class StoreUtil {
    private StoreUtil() {
    }

    @Nonnull
    static <Parsed, Key> Observable.Transformer<Parsed, Parsed>
    repeatWhenCacheEvicted(PublishSubject<Key> refreshSubject, @Nonnull final Key keyForRepeat) {
        Observable<Key> filter = refreshSubject.filter(new Func1<Key, Boolean>() {
            @Override
            public Boolean call(Key key) {
                return key.equals(keyForRepeat);
            }
        });
        return RepeatWhenEmits.from(filter);
    }

    static <Raw, Key> boolean shouldReturnNetworkBeforeStale(
            Persister<Raw, Key> persister, StalePolicy stalePolicy, Key key) {
        return stalePolicy == StalePolicy.NETWORK_BEFORE_STALE
                && persisterIsStale(key, persister);
    }

    static <Raw, Key> boolean persisterIsStale(@Nonnull Key key, Persister<Raw, Key> persister) {
        if (persister instanceof RecordProvider) {
            RecordProvider<Key> provider = (RecordProvider<Key>) persister;
            RecordState recordState = provider.getRecordState(key);
            return recordState == STALE;
        }
        return false;
    }

    static <Raw, Key> void clearPersister(Persister<Raw, Key> persister, @Nonnull Key key) {
        boolean isPersisterClearable = persister instanceof Clearable;

        if (isPersisterClearable) {
            ((Clearable<Key>) persister).clear(key);
        }
    }

    static <Key, Parsed> Cache<Key, Observable<Parsed>> initMemCache(@Nonnull MemoryPolicy memoryPolicy) {
        return CacheBuilder
                .newBuilder()
                .maximumSize(memoryPolicy.getMaxSize())
                .expireAfterWrite(memoryPolicy.getExpireAfter(), memoryPolicy.getExpireAfterTimeUnit())
                .build();
    }

    static <Key, Parsed> Cache<Key, Observable<Parsed>> initFlightRequests(@Nonnull MemoryPolicy memoryPolicy) {
        long expireAfterToSeconds = memoryPolicy.getExpireAfterTimeUnit().toSeconds(memoryPolicy.getExpireAfter());
        long maximumInFlightRequestsDuration = TimeUnit.MINUTES.toSeconds(1);

        if (expireAfterToSeconds > maximumInFlightRequestsDuration) {
            return CacheBuilder
                    .newBuilder()
                    .expireAfterWrite(maximumInFlightRequestsDuration, TimeUnit.SECONDS)
                    .build();
        } else {
            return CacheBuilder.newBuilder()
                    .expireAfterWrite(memoryPolicy.getExpireAfter(), memoryPolicy.getExpireAfterTimeUnit())
                    .build();
        }
    }

    /**
     * Default Cache TTL, can be overridden
     *
     * @return memory persister ttl
     */
    static long getCacheTTL() {
        return TimeUnit.HOURS.toSeconds(24);
    }

    /**
     * Default mem persister is 1, can be overridden otherwise
     *
     * @return memory persister size
     */
    static long getCacheSize() {
        return 100;
    }

    static TimeUnit getCacheTTLTimeUnit() {
        return TimeUnit.SECONDS;
    }
}
