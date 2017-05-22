package com.nytimes.android.external.cache3;



import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.nytimes.android.external.cache3.Preconditions.checkNotNull;

public final class RemovalNotification<K, V> implements Map.Entry<K, V> {

  private final K key;
    private final V value;
  @Nullable
  private final RemovalCause cause;

  /**
   * Creates a new {@code RemovalNotification} for the given {@code key}/{@code value} pair, with
   * the given {@code cause} for the removal. The {@code key} and/or {@code value} may be
   * {@code null} if they were already garbage collected.
   *
   * @since 19.0
   */
  @Nonnull
  public static <K, V> RemovalNotification<K, V> create(
          K key, V value, @Nonnull RemovalCause cause) {
    return new RemovalNotification(key, value, cause);
  }

  private RemovalNotification(  K key,   V value, @Nonnull RemovalCause cause) {
    this.key = key;
    this.value = value;
    this.cause = checkNotNull(cause);
  }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Returns the cause for which the entry was removed.
//   */
//  public RemovalCause getCause() {
//    return cause;
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
//   * {@link RemovalCause#EXPLICIT} nor {@link RemovalCause#REPLACED}).
//   */
//  public boolean wasEvicted() {
//    return cause.wasEvicted();
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

    @Override public K getKey() {
    return key;
  }

    @Override public V getValue() {
    return value;
  }

  @Nonnull
  @Override public final V setValue(V value) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean equals(  Object object) {
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
  @Nonnull
  @Override public String toString() {
    return getKey() + "=" + getValue();
  }
  // --Commented out by Inspection (11/29/16, 5:04 PM):private static final long serialVersionUID = 0;
}
