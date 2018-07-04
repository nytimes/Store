package com.nytimes.android.external.store3.storecache;


import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

public class StoreCacheBuilder<K, V> {

    static final int UNSET_INT = -1;
    long maximumSize = UNSET_INT;
    long expireAfterAccessDuration = -1;
    TimeUnit expireAfterAccessUnit = TimeUnit.MINUTES;
    boolean useExpireAfterWrite = true;
    long expireAfterWriteDuration = 8;
    TimeUnit expireAfterWriteUnit = TimeUnit.HOURS;
    TimeProvider timeProvider = () -> System.currentTimeMillis();

    @Nonnull
    public static StoreCacheBuilder<Object, Object> newBuilder() {
        return new StoreCacheBuilder<>();
    }

    @Nonnull
    public StoreCacheBuilder<K, V> maximumSize(long size) {
        this.maximumSize = size;
        return this;
    }

    @Nonnull
    public StoreCacheBuilder<K, V> expireAfterAccess(long expireAfterAccessDuration, TimeUnit expireAfterAccessUnit) {
        this.expireAfterAccessDuration = expireAfterAccessDuration;
        this.expireAfterAccessUnit = expireAfterAccessUnit;
        useExpireAfterWrite = false;
        return this;
    }

    @Nonnull
    public StoreCacheBuilder<K, V> expireAfterWrite(long expireAfterWriteDuration, TimeUnit expireAfterWriteUnit) {
        this.expireAfterWriteDuration = expireAfterWriteDuration;
        this.expireAfterWriteUnit = expireAfterWriteUnit;
        useExpireAfterWrite = true;
        return this;
    }

    @Nonnull
    public StoreCacheBuilder<K, V> timeProvider(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        return this;
    }

    @Nonnull
    public <K1 extends K, V1 extends V> StoreCache<K1, V1> build() {
        return new LocalStoreCache(this);
    }

}
