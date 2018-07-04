package com.nytimes.android.external.store3.storecache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

public interface StoreCache<K, V> {

    /*
     * "if cached, return; otherwise create, cache and return" pattern.
     */
    @Nullable
    V get(K key, Callable<? extends V> valueLoader) throws ExecutionException;


    void put(K key, V value);

    /**
     * Discards any cached value for key {@code key}.
     */
    void invalidate(Object key);

    void clearAll();

    @Nullable
    V getIfPresent(Object key);

    ConcurrentMap<K, V> asMap();

}
