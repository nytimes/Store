package com.nytimes.android.external.store3.storecache;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

class LocalStoreCache<K, V> implements StoreCache<K, V> {

    private final WriterLock writerLock = new WriterLock();
    private final Map<K, StoreRecord<V>> cache = new LinkedHashMap<>();

    private final long expDuration = 1;
    private final TimeUnit expUnit = TimeUnit.MINUTES;

    private final TimeProvider timeProvider;
    private final long maximumSize;

    private long count = 0;

    LocalStoreCache(StoreCacheBuilder storeCacheBuilder) {
        this.timeProvider = storeCacheBuilder.timeProvider;
        this.maximumSize = storeCacheBuilder.maximumSize;
    }

    @Nullable
    @Override
    public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {

        V returnValue = null;

        writerLock.getWriteLock();

        if (cache.containsKey(key)) {
            StoreRecord<V> record = cache.get(key);
            if (RecordPolicy.hasExpired(record, timeProvider.provideTime())) {
                // present AND expired...so we evict
                internalInvalidate(key);
            } else {
                //record is present and NOT expired
                record.setAccessTime(System.currentTimeMillis());
                returnValue = record.getValue();
            }
        }

        //not found in cache, use provided
        if (returnValue == null) {
            StoreRecord<V> record = null;
            try {
                record = internalPut(key, valueLoader.call());
            } catch (Exception exception) {
                throw new ExecutionException(exception);
            }
            returnValue = record.getValue();
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
        StoreRecord<V> record = StoreRecord.create(expDuration, expUnit);
        record.setValue(value);
        cache.put(key, record);
        count++;
        return record;
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
        V returnValue = null;

        writerLock.getWriteLock();
        if (cache.containsKey(key)) {
            StoreRecord<V> record = cache.get(key);
            if (RecordPolicy.hasExpired(record, timeProvider.provideTime())) {
                //it's expired, we invalidate
                internalInvalidate(key);
            } else {
                //it's valid, we update access time and return
                record.setAccessTime(System.currentTimeMillis());
                returnValue = record.getValue();
            }
        }
        writerLock.releaseLock();
        return returnValue;
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        ConcurrentMap<K, V> map;

        writerLock.getWriteLock();
        //do initial internalAsMap to prune expired
        ConcurrentMap<K, V> copy = internalAsMap();
        Iterator<K> iterator = copy.keySet().iterator();
        while (iterator.hasNext()) {
            K key = iterator.next();
            StoreRecord<V> record = cache.get(key);
            //prune out any expired entries
            if (RecordPolicy.hasExpired(record, timeProvider.provideTime())){
                internalInvalidate(key);
            }
        }
        //get pruned map and return that
        map = internalAsMap();
        writerLock.releaseLock();
        return map;
    }

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
