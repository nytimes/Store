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

    /**
     * Associates {@code value} with {@code key} in this cache. If the cache previously contained a
     * value associated with {@code key}, the old value is replaced by {@code value}.
     */
    void put(K key, V value);

    /**
     * Discards any cached value for key {@code key}.
     */
    void invalidate(Object key);

    /**
     * Discards all entries in the cache.
     */
    void clearAll();

    /**
     * Returns the value associated with {@code key} in this cache, or {@code null} if there is no
     * cached value for {@code key}.
     */
    @Nullable
    V getIfPresent(Object key);

    /**
     * Returns a view of the entries stored in this cache as a thread-safe map. Modifications made to
     * the map directly affect the cache.
     */
    ConcurrentMap<K, V> asMap();
}
