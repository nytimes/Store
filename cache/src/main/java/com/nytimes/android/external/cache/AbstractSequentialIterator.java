package com.nytimes.android.external.cache;




import java.util.NoSuchElementException;

public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T> {
  private T nextOrNull;

  /**
   * Creates a new iterator with the given first element, or, if {@code
   * firstOrNull} is null, creates a new empty iterator.
   */
  protected AbstractSequentialIterator(  T firstOrNull) {
    this.nextOrNull = firstOrNull;
  }

  /**
   * Returns the element that follows {@code previous}, or returns {@code null}
   * if no elements remain. This method is invoked during each call to
   * {@link #next()} in order to compute the result of a <i>future</i> call to
   * {@code next()}.
   */
  protected abstract T computeNext(T previous);

  @Override
  public final boolean hasNext() {
    return nextOrNull != null;
  }

  @Override
  public final T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    try {
      return nextOrNull;
    } finally {
      nextOrNull = computeNext(nextOrNull);
    }
  }
}
