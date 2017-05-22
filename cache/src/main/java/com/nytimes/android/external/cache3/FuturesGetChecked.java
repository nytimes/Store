package com.nytimes.android.external.cache3;


import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnull;

import static java.lang.Thread.currentThread;

final class FuturesGetChecked {
  static <V, X extends Exception> V getChecked(@Nonnull Future<V> future, Class<X> exceptionClass) throws X {
    return getChecked(bestGetCheckedTypeValidator(), future, exceptionClass);
  }

  /**
   * Implementation of {@link Futures#getChecked(Future, Class)}.
   */
  static <V, X extends Exception> V getChecked(
          @Nonnull GetCheckedTypeValidator validator, @Nonnull Future<V> future, Class<X> exceptionClass) throws X {
    validator.validateClass(exceptionClass);
    try {
      return future.get();
    } catch (InterruptedException e) {
      currentThread().interrupt();
      throw new RuntimeException();
    } catch (ExecutionException e) {
      throw new RuntimeException();
    }
  }

  /**
   * Implementation of {@link Futures#getChecked(Future, Class, long, TimeUnit)}.
   */
  static <V, X extends Exception> V getChecked(
          @Nonnull Future<V> future, Class<X> exceptionClass, long timeout, @Nonnull TimeUnit unit) throws X {
    // TODO(cpovirk): benchmark a version of this method that accepts a GetCheckedTypeValidator
    bestGetCheckedTypeValidator().validateClass(exceptionClass);
    try {
      return future.get(timeout, unit);
    } catch (InterruptedException e) {
      currentThread().interrupt();
      throw new RuntimeException();
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    } catch (TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  interface GetCheckedTypeValidator {
    void validateClass(Class<? extends Exception> exceptionClass);
  }

  @Nonnull
  private static GetCheckedTypeValidator bestGetCheckedTypeValidator() {
    return GetCheckedTypeValidatorHolder.BEST_VALIDATOR;
  }

  @Nonnull
  static GetCheckedTypeValidator weakSetValidator() {
    return GetCheckedTypeValidatorHolder.WeakSetValidator.INSTANCE;
  }

  /**
   * Provides a check of whether an exception type is valid for use with
   * {@link FuturesGetChecked#getChecked(Future, Class)}, possibly using caching.
   *
   * <p>Uses reflection to gracefully fall back to when certain implementations aren't available.
   */

  private FuturesGetChecked() {}

  static class GetCheckedTypeValidatorHolder {
    static final String CLASS_VALUE_VALIDATOR_NAME =
            GetCheckedTypeValidatorHolder.class.getName() + "$ClassValueValidator";

    static final GetCheckedTypeValidator BEST_VALIDATOR = getBestValidator();


    enum WeakSetValidator implements GetCheckedTypeValidator {
      INSTANCE;

      /*
       * Static final fields are presumed to be fastest, based on our experience with
       * UnsignedBytesBenchmark. TODO(cpovirk): benchmark this
       */
      /*
       * A CopyOnWriteArraySet<WeakReference> is faster than a newSetFromMap of a MapMaker map with
       * weakKeys() and concurrencyLevel(1), even up to at least 12 cached exception types.
       */
      private static final Set<WeakReference<Class<? extends Exception>>> validClasses =
              new CopyOnWriteArraySet<>();

      @Override
      public void validateClass(@Nonnull Class<? extends Exception> exceptionClass) {
        for (WeakReference<Class<? extends Exception>> knownGood : validClasses) {
          if (exceptionClass.equals(knownGood.get())) {
            return;
          }
          // TODO(cpovirk): if reference has been cleared, remove it?
        }

        /*
         * It's very unlikely that any loaded Futures class will see getChecked called with more
         * than a handful of exceptions. But it seems prudent to set a cap on how many we'll cache.
         * This avoids out-of-control memory consumption, and it keeps the cache from growing so
         * large that doing the lookup is noticeably slower than redoing the work would be.
         *
         * Ideally we'd have a real eviction policy, but until we see a problem in practice, I hope
         * that this will suffice. I have not even benchmarked with different size limits.
         */
        if (validClasses.size() > 1000) {
          validClasses.clear();
        }

        validClasses.add(new WeakReference<Class<? extends Exception>>(exceptionClass));
      }
    }

    /**
     * Returns the ClassValue-using validator, or falls back to the "weak Set" implementation if
     * unable to do so.
     */
    @Nonnull
    static GetCheckedTypeValidator getBestValidator() {
      try {
        Class<?> theClass = Class.forName(CLASS_VALUE_VALIDATOR_NAME);
        return (GetCheckedTypeValidator) theClass.getEnumConstants()[0];
      } catch (Throwable t) { // ensure we really catch *everything*
        return weakSetValidator();
      }
    }
  }

}
