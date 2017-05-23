package com.nytimes.android.external.cache3;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

public abstract class ForwardingCache<K, V> extends ForwardingObject implements Cache<K, V> {

  /** Constructor for use by subclasses. */
  protected ForwardingCache() {}

  @Override
  protected abstract Cache<K, V> delegate();

  /**
   * @since 11.0
   */
  @Nullable
  @Override
  public V getIfPresent(Object key) {
    return delegate().getIfPresent(key);
  }

  /**
   * @since 11.0
   */
  @Nullable
  @Override
  public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {
    return delegate().get(key, valueLoader);
  }

  /**
   * @since 11.0
   */
  @Override
  public Map<K, V> getAllPresent(Iterable<?> keys) {
    return delegate().getAllPresent(keys);
  }

  /**
   * @since 11.0
   */
  @Override
  public void put(K key, V value) {
    delegate().put(key, value);
  }

  /**
   * @since 12.0
   */
  @Override
  public void putAll(Map<? extends K,? extends V> m) {
    delegate().putAll(m);
  }

  @Override
  public void invalidate(Object key) {
    delegate().invalidate(key);
  }

  /**
   * @since 11.0
   */
  @Override
  public void invalidateAll(Iterable<?> keys) {
    delegate().invalidateAll(keys);
  }

  @Override
  public void invalidateAll() {
    delegate().invalidateAll();
  }

  @Override
  public long size() {
    return delegate().size();
  }


  @Override
  public ConcurrentMap<K, V> asMap() {
    return delegate().asMap();
  }

  @Override
  public void cleanUp() {
    delegate().cleanUp();
  }

}
