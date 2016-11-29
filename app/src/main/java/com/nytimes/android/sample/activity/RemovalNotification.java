package com.nytimes.android.sample.activity;

import android.support.annotation.Nullable;

import java.util.Map;

import static com.nytimes.android.sample.activity.Preconditions.checkNotNull;

public final class RemovalNotification<K, V> implements Map.Entry<K, V> {
  @Nullable
  private final K key;
  @Nullable private final V value;
  private final RemovalCause cause;

  /**
   * Creates a new {@code RemovalNotification} for the given {@code key}/{@code value} pair, with
   * the given {@code cause} for the removal. The {@code key} and/or {@code value} may be
   * {@code null} if they were already garbage collected.
   *
   * @since 19.0
   */
  public static <K, V> RemovalNotification<K, V> create(
      @Nullable K key, @Nullable V value, RemovalCause cause) {
    return new RemovalNotification(key, value, cause);
  }

  private RemovalNotification(@Nullable K key, @Nullable V value, RemovalCause cause) {
    this.key = key;
    this.value = value;
    this.cause = checkNotNull(cause);
  }

  /**
   * Returns the cause for which the entry was removed.
   */
  public RemovalCause getCause() {
    return cause;
  }

  /**
   * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
   * {@link RemovalCause#EXPLICIT} nor {@link RemovalCause#REPLACED}).
   */
  public boolean wasEvicted() {
    return cause.wasEvicted();
  }

  @Nullable @Override public K getKey() {
    return key;
  }

  @Nullable @Override public V getValue() {
    return value;
  }

  @Override public final V setValue(V value) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean equals(@Nullable Object object) {
    if (object instanceof Map.Entry) {
      Map.Entry<?, ?> that = (Map.Entry<?, ?>) object;
      return Objects.equal(this.getKey(), that.getKey())
          && Objects.equal(this.getValue(), that.getValue());
    }
    return false;
  }

  @Override public int hashCode() {
    K k = getKey();
    V v = getValue();
    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
  }

  /**
   * Returns a string representation of the form <code>{key}={value}</code>.
   */
  @Override public String toString() {
    return getKey() + "=" + getValue();
  }
  private static final long serialVersionUID = 0;
}
