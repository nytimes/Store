package com.nytimes.android.external.store3.storecache;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class LocalStoreCache<K, V> implements StoreCache<K, V> {

    private final WriterLock writerLock = new WriterLock();
    private final Map<K, StoreRecord<V>> cache = new LinkedHashMap<>();
    private final long expDuration;
    private final TimeUnit expUnit;
    private final RecordPolicy policy;
    private final TimeProvider timeProvider;
    private final long maximumSize;

    private long count = 0;

    LocalStoreCache(StoreCacheBuilder storeCacheBuilder) {
        this.timeProvider = storeCacheBuilder.timeProvider;
        this.maximumSize = storeCacheBuilder.maximumSize;

        if (storeCacheBuilder.useExpireAfterWrite) {
            policy = RecordPolicy.ExpireAfterWrite;
            expDuration = storeCacheBuilder.expireAfterWriteDuration;
            expUnit = storeCacheBuilder.expireAfterWriteUnit;
        } else {
            policy = RecordPolicy.ExpireAfterAccess;
            expDuration = storeCacheBuilder.expireAfterAccessDuration;
            expUnit = storeCacheBuilder.expireAfterAccessUnit;
        }
    }

    @Nullable
    @Override
    public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {

        V returnValue;
        writerLock.getWriteLock();
        returnValue = internalGetIfPresent(key);
        //not found in cache, use provided
        if (returnValue == null) {
            try {
                returnValue = internalPut(key, valueLoader.call()).getValue();
            } catch (Exception exception) {
                throw new ExecutionException(exception);
            }
        }
        writerLock.releaseLock();
        return returnValue;
    }

    @Override
    public void put(K key, V value) {
        writerLock.getWriteLock();
        internalPut(key, value);
        writerLock.releaseLock();
    }

    private StoreRecord<V> internalPut(K key, V value) {
        StoreRecord<V> record = new StoreRecord(policy, expDuration, expUnit, value);
        if (count == maximumSize) {
            evictOne();
        }
        cache.put(key, record);
        count++;
        return record;
    }

    private void evictOne() {
        //do impl
        System.currentTimeMillis();
    }

    private void internalInvalidate(Object key) {
        cache.remove(key);
        count--;
    }

    @Override
    public void invalidate(Object key) {
        writerLock.getWriteLock();
        internalInvalidate(key);
        writerLock.releaseLock();
    }

    @Override
    public void clearAll() {
        writerLock.getWriteLock();
        cache.clear();
        count = 0;
        writerLock.releaseLock();
    }

    @Nullable
    @Override
    public V getIfPresent(Object key) {
        V returnValue;
        writerLock.getWriteLock();
        returnValue = internalGetIfPresent(key);
        writerLock.releaseLock();
        return returnValue;
    }

    @Nullable
    private V internalGetIfPresent(Object key) {

        V returnValue = null;
        if (!cache.containsKey(key)) {
            return returnValue;
        }

        StoreRecord<V> record = cache.get(key);
        long now = timeProvider.provideTime();
        if (RecordPolicy.hasExpired(record, now)) {
            //it's expired, we invalidate
            internalInvalidate(key);
        } else {
            //it's valid, we update access time and return
            record.setAccessTime(now);
            returnValue = record.getValue();
        }
        return returnValue;
    }

    @Override
    @Nonnull
    public ConcurrentMap<K, V> asMap() {
        ConcurrentMap<K, V> map;
        writerLock.getWriteLock();
        pruneExpiredEntries();
        //get pruned map and return that
        map = internalAsMap();
        writerLock.releaseLock();
        return map;
    }

    private void pruneExpiredEntries() {
        //do initial internalAsMap to prune expired
        ConcurrentMap<K, V> copy = internalAsMap();
        Iterator<K> iterator = copy.keySet().iterator();
        long now = timeProvider.provideTime();
        while (iterator.hasNext()) {
            K key = iterator.next();
            StoreRecord<V> record = cache.get(key);
            //prune out any expired entries
            if (RecordPolicy.hasExpired(record, now)){
                internalInvalidate(key);
            }
        }
    }

    @Nonnull
    private ConcurrentMap<K, V> internalAsMap() {
        ConcurrentMap<K, V> map = new ConcurrentHashMap();
        Iterator<K> iterator = cache.keySet().iterator();
        while (iterator.hasNext()) {
            K key = iterator.next();
            V value = cache.get(key).getValue();
            map.put(key, value);
        }
        return map;
    }

}
