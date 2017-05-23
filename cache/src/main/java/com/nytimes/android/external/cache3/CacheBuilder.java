/*
 * Copyright (C) 2009 The Guava Authors
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

package com.nytimes.android.external.cache3;




import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class CacheBuilder<K, V> {
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
  private static final int DEFAULT_EXPIRATION_NANOS = 0;
  private static final int DEFAULT_REFRESH_NANOS = 0;

  enum NullListener implements RemovalListener<Object, Object> {
    INSTANCE;

    @Override
    public void onRemoval(RemovalNotification<Object, Object> notification) {}
  }

  enum OneWeigher implements Weigher<Object, Object> {
    INSTANCE;

    @Override
    public int weigh(Object key, Object value) {
      return 1;
    }
  }

  static final Ticker NULL_TICKER = new Ticker() {
    @Override
    public long read() {
      return 0;
    }
  };

  private static final Logger logger = Logger.getLogger(CacheBuilder.class.getName());

  static final int UNSET_INT = -1;

  boolean strictParsing = true;

  int initialCapacity = UNSET_INT;
  int concurrencyLevel = UNSET_INT;
  long maximumSize = UNSET_INT;
  long maximumWeight = UNSET_INT;
  Weigher<? super K, ? super V> weigher;

  LocalCache.Strength keyStrength;
  LocalCache.Strength valueStrength;

  long expireAfterWriteNanos = UNSET_INT;
  long expireAfterAccessNanos = UNSET_INT;
  long refreshNanos = UNSET_INT;

  Equivalence<Object> keyEquivalence;
  Equivalence<Object> valueEquivalence;

  RemovalListener<? super K, ? super V> removalListener;
  Ticker ticker;


  // TODO(fry): make constructor private and update tests to use newBuilder
  CacheBuilder() {}

  /**
   * Constructs a new {@code CacheBuilder} instance with default settings, including strong keys,
   * strong values, and no automatic eviction of any kind.
   */
  @Nonnull
  public static CacheBuilder<Object, Object> newBuilder() {
    return new CacheBuilder<>();
  }

  /**
   * Sets a custom {@code Equivalence} strategy for comparing keys.
   *
   * <p>By default, the cache uses {@link Equivalence#identity} to determine key equality when
   * @link #weakKeys} is specified, and {@link Equivalence#equals()} otherwise.
   */
  @Nonnull
  CacheBuilder<K, V> keyEquivalence(@Nonnull Equivalence<Object> equivalence) {
    Preconditions.checkState(keyEquivalence == null, "key equivalence was already set to %s", keyEquivalence);
    keyEquivalence = Preconditions.checkNotNull(equivalence);
    return this;
  }

  @Nullable
  Equivalence<Object> getKeyEquivalence() {
    return MoreObjects.firstNonNull(keyEquivalence, getKeyStrength().defaultEquivalence());
  }

  /**
   * Sets a custom {@code Equivalence} strategy for comparing values.
   *
   * <p>By default, the cache uses {@link Equivalence#identity} to determine value equality when
   * @link #weakValues} or @link #softValues} is specified, and {@link Equivalence#equals()}
   * otherwise.
   */
  @Nonnull
  CacheBuilder<K, V> valueEquivalence(@Nonnull Equivalence<Object> equivalence) {
    Preconditions.checkState(valueEquivalence == null,
        "value equivalence was already set to %s", valueEquivalence);
    this.valueEquivalence = Preconditions.checkNotNull(equivalence);
    return this;
  }

  @Nullable
  Equivalence<Object> getValueEquivalence() {
    return MoreObjects.firstNonNull(valueEquivalence, getValueStrength().defaultEquivalence());
  }

  int getInitialCapacity() {
    return (initialCapacity == UNSET_INT) ? DEFAULT_INITIAL_CAPACITY : initialCapacity;
  }

  /**
   * Guides the allowed concurrency among update operations. Used as a hint for internal sizing. The
   * table is internally partitioned to try to permit the indicated number of concurrent updates
   * without contention. Because assignment of entries to these partitions is not necessarily
   * uniform, the actual concurrency observed may vary. Ideally, you should choose a value to
   * accommodate as many threads as will ever concurrently modify the table. Using a significantly
   * higher value than you need can waste space and time, and a significantly lower value can lead
   * to thread contention. But overestimates and underestimates within an order of magnitude do not
   * usually have much noticeable impact. A value of one permits only one thread to modify the cache
   * at a time, but since read operations and cache loading computations can proceed concurrently,
   * this still yields higher concurrency than full synchronization.
   *
   * <p> Defaults to 4. <b>Note:</b>The default may change in the future. If you care about this
   * value, you should always choose it explicitly.
   *
   * <p>The current implementation uses the concurrency level to create a fixed number of hashtable
   * segments, each governed by its own write lock. The segment lock is taken once for each explicit
   * write, and twice for each cache loading computation (once prior to loading the new value,
   * and once after loading completes). Much internal cache management is performed at the segment
   * granularity. For example, access queues and write queues are kept per segment when they are
   * required by the selected eviction algorithm. As such, when writing unit tests it is not
   * uncommon to specify {@code concurrencyLevel(1)} in order to achieve more deterministic eviction
   * behavior.
   *
   * <p>Note that future implementations may abandon segment locking in favor of more advanced
   * concurrency controls.
   *
   * @throws IllegalArgumentException if {@code concurrencyLevel} is nonpositive
   * @throws IllegalStateException if a concurrency level was already set
   */
  @Nonnull
  public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
    Preconditions.checkState(this.concurrencyLevel == UNSET_INT, "concurrency level was already set to %s",
        this.concurrencyLevel);
    Preconditions.checkArgument(concurrencyLevel > 0);
    this.concurrencyLevel = concurrencyLevel;
    return this;
  }

  int getConcurrencyLevel() {
    return (concurrencyLevel == UNSET_INT) ? DEFAULT_CONCURRENCY_LEVEL : concurrencyLevel;
  }

  /**
   * Specifies the maximum number of entries the cache may contain. Note that the cache <b>may evict
   * an entry before this limit is exceeded</b>. As the cache size grows close to the maximum, the
   * cache evicts entries that are less likely to be used again. For example, the cache may evict an
   * entry because it hasn't been used recently or very often.
   *
   * <p>When {@code size} is zero, elements will be evicted immediately after being loaded into the
   * cache. This can be useful in testing, or to disable caching temporarily without a code change.
   *
   * <p>This feature cannot be used in conjunction with {@link #maximumWeight}.
   *
   * @param size the maximum size of the cache
   * @throws IllegalArgumentException if {@code size} is negative
   * @throws IllegalStateException if a maximum size or weight was already set
   */
  @Nonnull
  public CacheBuilder<K, V> maximumSize(long size) {
    Preconditions.checkState(this.maximumSize == UNSET_INT, "maximum size was already set to %s",
        this.maximumSize);
    Preconditions.checkState(this.maximumWeight == UNSET_INT, "maximum weight was already set to %s",
        this.maximumWeight);
    Preconditions.checkState(this.weigher == null, "maximum size can not be combined with weigher");
    Preconditions.checkArgument(size >= 0, "maximum size must not be negative");
    this.maximumSize = size;
    return this;
  }

  /**
   * Specifies the maximum weight of entries the cache may contain. Weight is determined using the
   * {@link Weigher} specified with {@link #weigher}, and use of this method requires a
   * corresponding call to {@link #weigher} prior to calling {@link #build}.
   *
   * <p>Note that the cache <b>may evict an entry before this limit is exceeded</b>. As the cache
   * size grows close to the maximum, the cache evicts entries that are less likely to be used
   * again. For example, the cache may evict an entry because it hasn't been used recently or very
   * often.
   *
   * <p>When {@code weight} is zero, elements will be evicted immediately after being loaded into
   * cache. This can be useful in testing, or to disable caching temporarily without a code
   * change.
   *
   * <p>Note that weight is only used to determine whether the cache is over capacity; it has no
   * effect on selecting which entry should be evicted next.
   *
   * <p>This feature cannot be used in conjunction with {@link #maximumSize}.
   *
   * @param weight the maximum total weight of entries the cache may contain
   * @throws IllegalArgumentException if {@code weight} is negative
   * @throws IllegalStateException if a maximum weight or size was already set
   * @since 11.0
   */
  @Nonnull
  public CacheBuilder<K, V> maximumWeight(long weight) {
    Preconditions.checkState(this.maximumWeight == UNSET_INT, "maximum weight was already set to %s",
        this.maximumWeight);
    Preconditions.checkState(this.maximumSize == UNSET_INT, "maximum size was already set to %s",
        this.maximumSize);
    this.maximumWeight = weight;
    Preconditions.checkArgument(weight >= 0, "maximum weight must not be negative");
    return this;
  }

  /**
   * Specifies the weigher to use in determining the weight of entries. Entry weight is taken
   * into consideration by {@link #maximumWeight(long)} when determining which entries to evict, and
   * use of this method requires a corresponding call to {@link #maximumWeight(long)} prior to
   * calling {@link #build}. Weights are measured and recorded when entries are inserted into the
   * cache, and are thus effectively static during the lifetime of a cache entry.
   *
   * <p>When the weight of an entry is zero it will not be considered for size-based eviction
   * (though it still may be evicted by other means).
   *
   * <p><b>Important note:</b> Instead of returning <em>this</em> as a {@code CacheBuilder}
   * instance, this method returns {@code CacheBuilder<K1, V1>}. From this point on, either the
   * original reference or the returned reference may be used to complete configuration and build
   * the cache, but only the "generic" one is type-safe. That is, it will properly prevent you from
   * building caches whose key or value types are incompatible with the types accepted by the
   * weigher already provided; the {@code CacheBuilder} type cannot do this. For best results,
   * simply use the standard method-chaining idiom, as illustrated in the documentation at top,
   * configuring a {@code CacheBuilder} and building your Cache all in a single statement.
   *
   * <p><b>Warning:</b> if you ignore the above advice, and use this {@code CacheBuilder} to build
   * a cache whose key or value type is incompatible with the weigher, you will likely experience
   * a {@link ClassCastException} at some <i>undefined</i> point in the future.
   *
   * @param weigher the weigher to use in calculating the weight of cache entries
   * @throws IllegalArgumentException if {@code size} is negative
   * @throws IllegalStateException if a maximum size was already set
   * @since 11.0
   */
  @Nonnull
  public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> weigher(
      @Nonnull Weigher<? super K1, ? super V1> weigher) {
    Preconditions.checkState(this.weigher == null);
    if (strictParsing) {
      Preconditions.checkState(this.maximumSize == UNSET_INT, "weigher can not be combined with maximum size",
          this.maximumSize);
    }

    // safely limiting the kinds of caches this can produce
    @SuppressWarnings("unchecked")
    CacheBuilder<K1, V1> me = (CacheBuilder<K1, V1>) this;
    me.weigher = Preconditions.checkNotNull(weigher);
    return me;
  }

  long getMaximumWeight() {
    if (expireAfterWriteNanos == 0 || expireAfterAccessNanos == 0) {
      return 0;
    }
    return (weigher == null) ? maximumSize : maximumWeight;
  }

  // Make a safe contravariant cast now so we don't have to do it over and over.
  @Nullable
  @SuppressWarnings("unchecked")
  <K1 extends K, V1 extends V> Weigher<K1, V1> getWeigher() {
    return (Weigher<K1, V1>) MoreObjects.firstNonNull(weigher, OneWeigher.INSTANCE);
  }


  @Nonnull
  CacheBuilder<K, V> setKeyStrength(@Nonnull LocalCache.Strength strength) {
    Preconditions.checkState(keyStrength == null, "Key strength was already set to %s", keyStrength);
    keyStrength = Preconditions.checkNotNull(strength);
    return this;
  }

  @Nullable
  LocalCache.Strength getKeyStrength() {
    return MoreObjects.firstNonNull(keyStrength, LocalCache.Strength.STRONG);
  }


  @Nonnull
  CacheBuilder<K, V> setValueStrength(@Nonnull LocalCache.Strength strength) {
    Preconditions.checkState(valueStrength == null, "Value strength was already set to %s", valueStrength);
    valueStrength = Preconditions.checkNotNull(strength);
    return this;
  }

  @Nullable
  LocalCache.Strength getValueStrength() {
    return MoreObjects.firstNonNull(valueStrength, LocalCache.Strength.STRONG);
  }

  /**
   * Specifies that each entry should be automatically removed from the cache once a fixed duration
   * has elapsed after the entry's creation, or the most recent replacement of its value.
   *
   * <p>When {@code duration} is zero, this method hands off to
   * {@link #maximumSize(long) maximumSize}{@code (0)}, ignoring any otherwise-specificed maximum
   * size or weight. This can be useful in testing, or to disable caching temporarily without a code
   * change.
   *
   * <p>Expired entries may be counted in @link Cache#size}, but will never be visible to read or
   * write operations. Expired entries are cleaned up as part of the routine maintenance described
   * in the class javadoc.
   *
   * @param duration the length of time after an entry is created that it should be automatically
   *     removed
   * @param unit the unit that {@code duration} is expressed in
   * @throws IllegalArgumentException if {@code duration} is negative
   * @throws IllegalStateException if the time to live or time to idle was already set
   */
  @Nonnull
  public CacheBuilder<K, V> expireAfterWrite(long duration, @Nonnull TimeUnit unit) {
    Preconditions.checkState(expireAfterWriteNanos == UNSET_INT, "expireAfterWrite was already set to %s ns",
        expireAfterWriteNanos);
    Preconditions.checkArgument(duration >= 0, "duration cannot be negative: %s %s", duration, unit);
    this.expireAfterWriteNanos = unit.toNanos(duration);
    return this;
  }

  long getExpireAfterWriteNanos() {
    return (expireAfterWriteNanos == UNSET_INT) ? DEFAULT_EXPIRATION_NANOS : expireAfterWriteNanos;
  }

  /**
   * Specifies that each entry should be automatically removed from the cache once a fixed duration
   * has elapsed after the entry's creation, the most recent replacement of its value, or its last
   * access. Access time is reset by all cache read and write operations (including
   * {@code Cache.asMap().get(Object)} and {@code Cache.asMap().put(K, V)}), but not by operations
   * on the collection-views of @link Cache#asMap}.
   *
   * <p>When {@code duration} is zero, this method hands off to
   * {@link #maximumSize(long) maximumSize}{@code (0)}, ignoring any otherwise-specificed maximum
   * size or weight. This can be useful in testing, or to disable caching temporarily without a code
   * change.
   *
   * <p>Expired entries may be counted in @link Cache#size}, but will never be visible to read or
   * write operations. Expired entries are cleaned up as part of the routine maintenance described
   * in the class javadoc.
   *
   * @param duration the length of time after an entry is last accessed that it should be
   *     automatically removed
   * @param unit the unit that {@code duration} is expressed in
   * @throws IllegalArgumentException if {@code duration} is negative
   * @throws IllegalStateException if the time to idle or time to live was already set
   */
  @Nonnull
  public CacheBuilder<K, V> expireAfterAccess(long duration, @Nonnull TimeUnit unit) {
    Preconditions.checkState(expireAfterAccessNanos == UNSET_INT, "expireAfterAccess was already set to %s ns",
        expireAfterAccessNanos);
    Preconditions.checkArgument(duration >= 0, "duration cannot be negative: %s %s", duration, unit);
    this.expireAfterAccessNanos = unit.toNanos(duration);
    return this;
  }

  long getExpireAfterAccessNanos() {
    return (expireAfterAccessNanos == UNSET_INT)
        ? DEFAULT_EXPIRATION_NANOS : expireAfterAccessNanos;
  }


  long getRefreshNanos() {
    return (refreshNanos == UNSET_INT) ? DEFAULT_REFRESH_NANOS : refreshNanos;
  }

  /**
   * Specifies a nanosecond-precision time source for this cache. By default,
   * {@link System#nanoTime} is used.
   *
   * <p>The primary intent of this method is to facilitate testing of caches with a fake or mock
   * time source.
   *
   * @throws IllegalStateException if a ticker was already set
   */
  @Nonnull
  public CacheBuilder<K, V> ticker(@Nonnull Ticker ticker) {
    Preconditions.checkState(this.ticker == null);
    this.ticker = Preconditions.checkNotNull(ticker);
    return this;
  }

  Ticker getTicker(boolean recordsTime) {
    if (ticker != null) {
      return ticker;
    }
    return recordsTime ? Ticker.systemTicker() : NULL_TICKER;
  }

  /**
   * Specifies a listener instance that caches should notify each time an entry is removed for any
   * {@linkplain RemovalCause reason}. Each cache created by this builder will invoke this listener
   * as part of the routine maintenance described in the class documentation above.
   *
   * <p><b>Warning:</b> after invoking this method, do not continue to use <i>this</i> cache
   * builder reference; instead use the reference this method <i>returns</i>. At runtime, these
   * point to the same instance, but only the returned reference has the correct generic type
   * information so as to ensure type safety. For best results, use the standard method-chaining
   * idiom illustrated in the class documentation above, configuring a builder and building your
   * cache in a single statement. Failure to heed this advice can result in a {@link
   * ClassCastException} being thrown by a cache operation at some <i>undefined</i> point in the
   * future.
   *
   * <p><b>Warning:</b> any exception thrown by {@code listener} will <i>not</i> be propagated to
   * the {@code Cache} user, only logged via a {@link Logger}.
   *
   * @return the cache builder reference that should be used instead of {@code this} for any
   *     remaining configuration and cache building
   * @throws IllegalStateException if a removal listener was already set
   */
  @Nonnull
  public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> removalListener(
      @Nonnull RemovalListener<? super K1, ? super V1> listener) {
    Preconditions.checkState(this.removalListener == null);

    // safely limiting the kinds of caches this can produce
    @SuppressWarnings("unchecked")
    CacheBuilder<K1, V1> me = (CacheBuilder<K1, V1>) this;
    me.removalListener = Preconditions.checkNotNull(listener);
    return me;
  }

  // Make a safe contravariant cast now so we don't have to do it over and over.
  @Nullable
  @SuppressWarnings("unchecked")
  <K1 extends K, V1 extends V> RemovalListener<K1, V1> getRemovalListener() {
    return (RemovalListener<K1, V1>)
        MoreObjects.firstNonNull(removalListener, NullListener.INSTANCE);
  }

  /**
   * Builds a cache, which either returns an already-loaded value for a given key or atomically
   * computes or retrieves it using the supplied {@code CacheLoader}. If another thread is currently
   * loading the value for this key, simply waits for that thread to finish and returns its
   * loaded value. Note that multiple threads can concurrently load values for distinct keys.
   *
   * <p>This method does not alter the state of this {@code CacheBuilder} instance, so it can be
   * invoked again to create multiple independent caches.
   *
   * @param loader the cache loader used to obtain new values
   * @return a cache having the requested features
   */
  @Nonnull
  public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(
          @Nonnull CacheLoader<? super K1, V1> loader) {
    checkWeightWithWeigher();
    return new LocalCache.LocalLoadingCache<>(this, loader);
  }

  @Nonnull
  public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
    checkWeightWithWeigher();
    checkNonLoadingCache();
    return new LocalCache.LocalManualCache<>(this);
  }



  private void checkNonLoadingCache() {
    Preconditions.checkState(refreshNanos == UNSET_INT, "refreshAfterWrite requires a LoadingCache");
  }

  private void checkWeightWithWeigher() {
    if (weigher == null) {
      Preconditions.checkState(maximumWeight == UNSET_INT, "maximumWeight requires weigher");
    } else {
      if (strictParsing) {
        Preconditions.checkState(maximumWeight != UNSET_INT, "weigher requires maximumWeight");
      } else {
        if (maximumWeight == UNSET_INT) {
          logger.log(Level.WARNING, "ignoring weigher specified without maximumWeight");
        }
      }
    }
  }

  /**
   * Returns a string representation for this CacheBuilder instance. The exact form of the returned
   * string is not specified.
   */
  @Override
  public String toString() {
    MoreObjects.ToStringHelper s = MoreObjects.toStringHelper(this);
    if (initialCapacity != UNSET_INT) {
      s.add("initialCapacity", initialCapacity);
    }
    if (concurrencyLevel != UNSET_INT) {
      s.add("concurrencyLevel", concurrencyLevel);
    }
    if (maximumSize != UNSET_INT) {
      s.add("maximumSize", maximumSize);
    }
    if (maximumWeight != UNSET_INT) {
      s.add("maximumWeight", maximumWeight);
    }
    if (expireAfterWriteNanos != UNSET_INT) {
      s.add("expireAfterWrite", expireAfterWriteNanos + "ns");
    }
    if (expireAfterAccessNanos != UNSET_INT) {
      s.add("expireAfterAccess", expireAfterAccessNanos + "ns");
    }
    if (keyStrength != null) {
      s.add("keyStrength", Ascii.toLowerCase(keyStrength.toString()));
    }
    if (valueStrength != null) {
      s.add("valueStrength", Ascii.toLowerCase(valueStrength.toString()));
    }
    if (keyEquivalence != null) {
      s.addValue("keyEquivalence");
    }
    if (valueEquivalence != null) {
      s.addValue("valueEquivalence");
    }
    if (removalListener != null) {
      s.addValue("removalListener");
    }
    return s.toString();
  }
}
