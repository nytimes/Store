package com.nytimes.android.external.cache;




public final class SettableFuture<V> extends AbstractFuture.TrustedFuture<V> {

  /**
   * Creates a new {@code SettableFuture} in the default state.
   */
  public static <V> SettableFuture<V> create() {
    return new SettableFuture<V>();
  }

  /**
   * Explicit private constructor, use the {@link #create} factory method to
   * create instances of {@code SettableFuture}.
   */
  private SettableFuture() {}

  @Override public boolean set(  V value) {
    return super.set(value);
  }

  @Override public boolean setException(Throwable throwable) {
    return super.setException(throwable);
  }

  @Override
  public boolean setFuture(ListenableFuture<? extends V> future) {
    return super.setFuture(future);
  }
}
