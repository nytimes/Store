package com.nytimes.android.external.cache3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

public final class Uninterruptibles {

  /**
   * Invokes {@code future.}{@link Future#get() get()} uninterruptibly.
   * To get uninterruptibility and remove checked exceptions, see
   * @link Futures#getUnchecked}.
   *
   * <p>If instead, you wish to treat {@link InterruptedException} uniformly
   * with other exceptions, see @link Futures#getChecked(Future, Class)
   * Futures.getChecked}.
   *
   * @throws ExecutionException if the computation threw an exception
   */
  public static <V> V getUninterruptibly(@Nonnull Future<V> future)
      throws ExecutionException {
    boolean interrupted = false;
    try {
      while (true) {
        try {
          return future.get();
        } catch (InterruptedException e) {
          interrupted = true;
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }


  private Uninterruptibles() {}
}
