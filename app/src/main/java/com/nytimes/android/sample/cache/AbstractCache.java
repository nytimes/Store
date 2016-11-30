/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nytimes.android.sample.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;



public abstract class AbstractCache<K, V> implements Cache<K, V> {

    /**
     * Constructor for use by subclasses.
     */
    protected AbstractCache() {
    }

    /**
     * @since 11.0
     */
    @Override
    public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<K, V> getAllPresent(Iterable<?> keys) {
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Object key : keys) {
            if (!result.containsKey(key)) {
                @SuppressWarnings("unchecked")
                K castKey = (K) key;
                V value = getIfPresent(key);
                if (value != null) {
                    result.put(castKey, value);
                }
            }
        }
        return result;
    }

    /**
     * @since 11.0
     */
    @Override
    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 12.0
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 11.0
     */
    @Override
    public void invalidateAll(Iterable<?> keys) {
        for (Object key : keys) {
            invalidate(key);
        }
    }

    @Override
    public void invalidateAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheStats stats() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        throw new UnsupportedOperationException();
    }


    public interface StatsCounter {
        /**
         * Records cache hits. This should be called when a cache request returns a cached value.
         *
         * @param count the number of hits to record
         * @since 11.0
         */
        void recordHits(int count);

        /**
         * Records cache misses. This should be called when a cache request returns a value that was
         * not found in the cache. This method should be called by the loading thread, as well as by
         * threads blocking on the load. Multiple concurrent calls to {@link Cache} lookup methods with
         * the same key on an absent value should result in a single call to either
         * {@code recordLoadSuccess} or {@code recordLoadException} and multiple calls to this method,
         * despite all being served by the results of a single load operation.
         *
         * @param count the number of misses to record
         * @since 11.0
         */
        void recordMisses(int count);

        /**
         * Records the successful load of a new entry. This should be called when a cache request
         * causes an entry to be loaded, and the loading completes successfully. In contrast to
         * {@link #recordMisses}, this method should only be called by the loading thread.
         *
         * @param loadTime the number of nanoseconds the cache spent computing or retrieving the new
         *                 value
         */
        void recordLoadSuccess(long loadTime);

        /**
         * Records the failed load of a new entry. This should be called when a cache request causes
         * an entry to be loaded, but an exception is thrown while loading the entry. In contrast to
         * {@link #recordMisses}, this method should only be called by the loading thread.
         *
         * @param loadTime the number of nanoseconds the cache spent computing or retrieving the new
         *                 value prior to an exception being thrown
         */
        void recordLoadException(long loadTime);

        /**
         * Records the eviction of an entry from the cache. This should only been called when an entry
         * is evicted due to the cache's eviction strategy, and not as a result of manual {@linkplain
         * Cache#invalidate invalidations}.
         */
        void recordEviction();

        /**
         * Returns a snapshot of this counter's values. Note that this may be an inconsistent view, as
         * it may be interleaved with update operations.
         */
        CacheStats snapshot();
    }

    /**
     * A thread-safe {@link StatsCounter} implementation for use by {@link Cache} implementors.
     *
     * @since 10.0
     */
    public static final class SimpleStatsCounter implements StatsCounter {
        private Long hitCount = 0l;
        private Long missCount = 0l;
        private Long loadSuccessCount = 0l;
        private Long loadExceptionCount = 0l;
        private Long totalLoadTime = 0l;
        private Long evictionCount = 0l;
        ;

        /**
         * Constructs an instance with all counts initialized to zero.
         */
        public SimpleStatsCounter() {
        }

        /**
         * @since 11.0
         */
        @Override
        public void recordHits(int count) {
            hitCount += count;
        }

        /**
         * @since 11.0
         */
        @Override
        public void recordMisses(int count) {
            missCount+=count;
        }

        @Override
        public void recordLoadSuccess(long loadTime) {
            loadSuccessCount++;
            totalLoadTime+=loadTime;
        }

        @Override
        public void recordLoadException(long loadTime) {
            loadExceptionCount++;
            totalLoadTime+=loadTime;
        }

        @Override
        public void recordEviction() {
            evictionCount++;
        }

        @Override
        public CacheStats snapshot() {
            return new CacheStats(
                    hitCount,
                    missCount,
                    loadSuccessCount,
                    loadExceptionCount,
                    totalLoadTime,
                    evictionCount);
        }

        /**
         * Increments all counters by the values in {@code other}.
         */
        public void incrementBy(StatsCounter other) {
            CacheStats otherStats = other.snapshot();
            hitCount+=otherStats.hitCount();
            missCount+=otherStats.missCount();
            loadSuccessCount+=otherStats.loadSuccessCount();
            loadExceptionCount+=otherStats.loadExceptionCount();
            totalLoadTime+=otherStats.totalLoadTime();
            evictionCount+=otherStats.evictionCount();
        }
    }
}
