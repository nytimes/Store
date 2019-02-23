package com.nytimes.android.external.store3.base.impl;

import com.nytimes.android.external.cache3.Cache;
import com.nytimes.android.external.cache3.CacheBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public final class CacheFactory {
    private CacheFactory() {

    }

    static <Key, Parsed> Cache<Key, Parsed> createCache(MemoryPolicy memoryPolicy) {
        return createBaseCache(memoryPolicy);
    }

    static <Key, Parsed> Cache<Key, Parsed> createInflighter(MemoryPolicy memoryPolicy) {
        return createBaseInFlighter(memoryPolicy);
    }

    public static <Key, Parsed> Cache<Key, Observable<Parsed>> createRoomCache(MemoryPolicy memoryPolicy) {
        return createBaseCache(memoryPolicy);
    }


    public static <Key, Parsed> Cache<Key, Observable<Parsed>> createRoomInflighter(MemoryPolicy memoryPolicy) {
        return createBaseInFlighter(memoryPolicy);
    }


    private static <Key, Value> Cache<Key, Value> createBaseInFlighter(MemoryPolicy memoryPolicy) {
        long expireAfterToSeconds = memoryPolicy == null ? StoreDefaults.getCacheTTLTimeUnit()
                .toSeconds(StoreDefaults.getCacheTTL())
                : memoryPolicy.getExpireAfterTimeUnit().toSeconds(memoryPolicy.getExpireAfterWrite());
        long maximumInFlightRequestsDuration = TimeUnit.MINUTES.toSeconds(1);

        if (expireAfterToSeconds > maximumInFlightRequestsDuration) {
            return CacheBuilder
                    .newBuilder()
                    .expireAfterWrite(maximumInFlightRequestsDuration, TimeUnit.SECONDS)
                    .build();
        } else {
            long expireAfter = memoryPolicy == null ? StoreDefaults.getCacheTTL() :
                    memoryPolicy.getExpireAfterWrite();
            TimeUnit expireAfterUnit = memoryPolicy == null ? StoreDefaults.getCacheTTLTimeUnit() :
                    memoryPolicy.getExpireAfterTimeUnit();
            return CacheBuilder.newBuilder()
                    .expireAfterWrite(expireAfter, expireAfterUnit)
                    .build();
        }
    }


    private static <Key, Value> Cache<Key, Value> createBaseCache(MemoryPolicy memoryPolicy) {
        if (memoryPolicy == null) {
            return CacheBuilder
                    .newBuilder()
                    .maximumSize(StoreDefaults.getCacheSize())
                    .expireAfterWrite(StoreDefaults.getCacheTTL(), StoreDefaults.getCacheTTLTimeUnit())
                    .build();
        } else {
            if (memoryPolicy.getExpireAfterAccess() == memoryPolicy.DEFAULT_POLICY) {
                return CacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.getMaxSize())
                        .expireAfterWrite(memoryPolicy.getExpireAfterWrite(), memoryPolicy.getExpireAfterTimeUnit())
                        .build();
            } else {
                return CacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.getMaxSize())
                        .expireAfterAccess(memoryPolicy.getExpireAfterAccess(), memoryPolicy.getExpireAfterTimeUnit())
                        .build();
            }
        }
    }

}
