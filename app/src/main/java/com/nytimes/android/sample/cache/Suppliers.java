package com.nytimes.android.sample.cache;

import android.support.annotation.Nullable;

import java.io.Serializable;

public final class Suppliers {
  private Suppliers() {}

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Returns a new supplier which is the composition of the provided function
//   * and supplier. In other words, the new supplier's value will be computed by
//   * retrieving the value from {@code supplier}, and then applying
//   * {@code function} to that value. Note that the resulting supplier will not
//   * call {@code supplier} or invoke {@code function} until it is called.
//   */
//  public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
//    Preconditions.checkNotNull(function);
//    Preconditions.checkNotNull(supplier);
//    return new SupplierComposition<F, T>(function, supplier);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

  private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {
    final Function<? super F, T> function;
    final Supplier<F> supplier;

    SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
      this.function = function;
      this.supplier = supplier;
    }

    @Override
    public T get() {
      return function.apply(supplier.get());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof SupplierComposition) {
        SupplierComposition<?, ?> that = (SupplierComposition<?, ?>) obj;
        return function.equals(that.function) && supplier.equals(that.supplier);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(function, supplier);
    }

    @Override
    public String toString() {
      return "Suppliers.compose(" + function + ", " + supplier + ")";
    }

    private static final long serialVersionUID = 0;
  }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Returns a supplier which caches the instance retrieved during the first
//   * call to {@code get()} and returns that value on subsequent calls to
//   * {@code get()}. See:
//   * <a href="http://en.wikipedia.org/wiki/Memoization">memoization</a>
//   *
//   * <p>The returned supplier is thread-safe. The delegate's {@code get()}
//   * method will be invoked at most once. The supplier's serialized form does
//   * not contain the cached value, which will be recalculated when {@code get()}
//   * is called on the reserialized instance.
//   *
//   * <p>If {@code delegate} is an instance created by an earlier call to {@code
//   * memoize}, it is returned directly.
//   */
//  public static <T> Supplier<T> memoize(Supplier<T> delegate) {
//    return (delegate instanceof MemoizingSupplier)
//        ? delegate
//        : new MemoizingSupplier<T>(Preconditions.checkNotNull(delegate));
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)


// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Returns a supplier that caches the instance supplied by the delegate and
//   * removes the cached value after the specified time has passed. Subsequent
//   * calls to {@code get()} return the cached value if the expiration time has
//   * not passed. After the expiration time, a new value is retrieved, cached,
//   * and returned. See:
//   * <a href="http://en.wikipedia.org/wiki/Memoization">memoization</a>
//   *
//   * <p>The returned supplier is thread-safe. The supplier's serialized form
//   * does not contain the cached value, which will be recalculated when {@code
//   * get()} is called on the reserialized instance.
//   *
//   * @param duration the length of time after a value is created that it
//   *     should stop being returned by subsequent {@code get()} calls
//   * @param unit the unit that {@code duration} is expressed in
//   * @throws IllegalArgumentException if {@code duration} is not positive
//   * @since 2.0
//   */
//  public static <T> Supplier<T> memoizeWithExpiration(
//      Supplier<T> delegate, long duration, TimeUnit unit) {
//    return new ExpiringMemoizingSupplier<T>(delegate, duration, unit);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)



  /**
   * Returns a supplier that always supplies {@code instance}.
   */
  public static <T> Supplier<T> ofInstance(@Nullable T instance) {
    return new SupplierOfInstance<T>(instance);
  }

  private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
    final T instance;

    SupplierOfInstance(@Nullable T instance) {
      this.instance = instance;
    }

    @Override
    public T get() {
      return instance;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof SupplierOfInstance) {
        SupplierOfInstance<?> that = (SupplierOfInstance<?>) obj;
        return Objects.equal(instance, that.instance);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(instance);
    }

    @Override
    public String toString() {
      return "Suppliers.ofInstance(" + instance + ")";
    }

    private static final long serialVersionUID = 0;
  }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Returns a supplier whose {@code get()} method synchronizes on
//   * {@code delegate} before calling it, making it thread-safe.
//   */
//  public static <T> Supplier<T> synchronizedSupplier(Supplier<T> delegate) {
//    return new ThreadSafeSupplier<T>(Preconditions.checkNotNull(delegate));
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)



// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Returns a function that accepts a supplier and returns the result of
//   * invoking {@link Supplier#get} on that supplier.
//   *
//   * @since 8.0
//   */
//  @Beta
//  public static <T> Function<Supplier<T>, T> supplierFunction() {
//    @SuppressWarnings("unchecked") // implementation is "fully variant"
//    SupplierFunction<T> sf = (SupplierFunction<T>) SupplierFunctionImpl.INSTANCE;
//    return sf;
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

  private interface SupplierFunction<T> extends Function<Supplier<T>, T> {}

}
