package com.nytimes.android.external.cache3;


import javax.annotation.Nonnull;

public final class SettableFuture<V> extends com.nytimes.android.external.cache3.AbstractFuture.TrustedFuture<V> {

  /**
   * Creates a new {@code SettableFuture} in the default state.
   */
  @Nonnull
  public static <V> SettableFuture<V> create() {
    return new SettableFuture<>();
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
  public boolean setFuture(@Nonnull com.nytimes.android.external.cache3.ListenableFuture<? extends V> future) {
    return super.setFuture(future);
  }
}
