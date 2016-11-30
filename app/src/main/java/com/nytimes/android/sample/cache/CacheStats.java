package com.nytimes.android.sample.cache;

import android.support.annotation.Nullable;

import static com.nytimes.android.sample.cache.Preconditions.checkArgument;

public final class CacheStats {
  private final long hitCount;
  private final long missCount;
  private final long loadSuccessCount;
  private final long loadExceptionCount;
  private final long totalLoadTime;
  private final long evictionCount;

  /**
   * Constructs a new {@code CacheStats} instance.
   *
   * <p>Five parameters of the same type in a row is a bad thing, but this class is not constructed
   * by end users and is too fine-grained for a builder.
   */
  public CacheStats(long hitCount, long missCount, long loadSuccessCount,
      long loadExceptionCount, long totalLoadTime, long evictionCount) {
    Preconditions.checkArgument(hitCount >= 0);
    Preconditions.checkArgument(missCount >= 0);
    Preconditions.checkArgument(loadSuccessCount >= 0);
    Preconditions.checkArgument(loadExceptionCount >= 0);
    Preconditions.checkArgument(totalLoadTime >= 0);
    Preconditions.checkArgument(evictionCount >= 0);

    this.hitCount = hitCount;
    this.missCount = missCount;
    this.loadSuccessCount = loadSuccessCount;
    this.loadExceptionCount = loadExceptionCount;
    this.totalLoadTime = totalLoadTime;
    this.evictionCount = evictionCount;
  }


  public long requestCount() {
    return hitCount + missCount;
  }


  public long hitCount() {
    return hitCount;
  }

// --Commented out by Inspection START (11/29/16, 5:02 PM):
//  /**
//   * Returns the ratio of cache requests which were hits. This is defined as
//   * {@code hitCount / requestCount}, or {@code 1.0} when {@code requestCount == 0}.
//   * Note that {@code hitRate + missRate =~ 1.0}.
//   */
//  public double hitRate() {
//    long requestCount = requestCount();
//    return (requestCount == 0) ? 1.0 : (double) hitCount / requestCount;
//  }
// --Commented out by Inspection STOP (11/29/16, 5:02 PM)


  public long missCount() {
    return missCount;
  }

// --Commented out by Inspection START (11/29/16, 5:02 PM):
//  /**
//   * Returns the ratio of cache requests which were misses. This is defined as
//   * {@code missCount / requestCount}, or {@code 0.0} when {@code requestCount == 0}.
//   * Note that {@code hitRate + missRate =~ 1.0}. Cache misses include all requests which
//   * weren't cache hits, including requests which resulted in either successful or failed loading
//   * attempts, and requests which waited for other threads to finish loading. It is thus the case
//   * that {@code missCount &gt;= loadSuccessCount + loadExceptionCount}. Multiple
//   * concurrent misses for the same key will result in a single load operation.
//   */
//  public double missRate() {
//    long requestCount = requestCount();
//    return (requestCount == 0) ? 0.0 : (double) missCount / requestCount;
//  }
// --Commented out by Inspection STOP (11/29/16, 5:02 PM)


// --Commented out by Inspection START (11/29/16, 5:02 PM):
//  public long loadCount() {
//    return loadSuccessCount + loadExceptionCount;
//  }
// --Commented out by Inspection STOP (11/29/16, 5:02 PM)


  public long loadSuccessCount() {
    return loadSuccessCount;
  }


  public long loadExceptionCount() {
    return loadExceptionCount;
  }

// --Commented out by Inspection START (11/29/16, 5:02 PM):
//  /**
//   * Returns the ratio of cache loading attempts which threw exceptions. This is defined as
//   * {@code loadExceptionCount / (loadSuccessCount + loadExceptionCount)}, or
//   * {@code 0.0} when {@code loadSuccessCount + loadExceptionCount == 0}.
//   */
//  public double loadExceptionRate() {
//    long totalLoadCount = loadSuccessCount + loadExceptionCount;
//    return (totalLoadCount == 0)
//        ? 0.0
//        : (double) loadExceptionCount / totalLoadCount;
//  }
// --Commented out by Inspection STOP (11/29/16, 5:02 PM)

  /**
   * Returns the total number of nanoseconds the cache has spent loading new values. This can be
   * used to calculate the miss penalty. This value is increased every time
   * {@code loadSuccessCount} or {@code loadExceptionCount} is incremented.
   */
  public long totalLoadTime() {
    return totalLoadTime;
  }

// --Commented out by Inspection START (11/29/16, 5:02 PM):
//  /**
//   * Returns the average time spent loading new values. This is defined as
//   * {@code totalLoadTime / (loadSuccessCount + loadExceptionCount)}.
//   */
//  public double averageLoadPenalty() {
//    long totalLoadCount = loadSuccessCount + loadExceptionCount;
//    return (totalLoadCount == 0)
//        ? 0.0
//        : (double) totalLoadTime / totalLoadCount;
//  }
// --Commented out by Inspection STOP (11/29/16, 5:02 PM)


  public long evictionCount() {
    return evictionCount;
  }

// --Commented out by Inspection START (11/29/16, 5:02 PM):
//  /**
//   * Returns a new {@code CacheStats} representing the difference between this {@code CacheStats}
//   * and {@code other}. Negative values, which aren't supported by {@code CacheStats} will be
//   * rounded up to zero.
//   */
//  public CacheStats minus(CacheStats other) {
//    return new CacheStats(
//        Math.max(0, hitCount - other.hitCount),
//        Math.max(0, missCount - other.missCount),
//        Math.max(0, loadSuccessCount - other.loadSuccessCount),
//        Math.max(0, loadExceptionCount - other.loadExceptionCount),
//        Math.max(0, totalLoadTime - other.totalLoadTime),
//        Math.max(0, evictionCount - other.evictionCount));
//  }
// --Commented out by Inspection STOP (11/29/16, 5:02 PM)

// --Commented out by Inspection START (11/29/16, 5:02 PM):
//  /**
//   * Returns a new {@code CacheStats} representing the sum of this {@code CacheStats}
//   * and {@code other}.
//   *
//   * @since 11.0
//   */
//  public CacheStats plus(CacheStats other) {
//    return new CacheStats(
//        hitCount + other.hitCount,
//        missCount + other.missCount,
//        loadSuccessCount + other.loadSuccessCount,
//        loadExceptionCount + other.loadExceptionCount,
//        totalLoadTime + other.totalLoadTime,
//        evictionCount + other.evictionCount);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:02 PM)

  @Override
  public int hashCode() {
    return Objects.hashCode(hitCount, missCount, loadSuccessCount, loadExceptionCount,
        totalLoadTime, evictionCount);
  }

  @Override
  public boolean equals(@Nullable Object object) {
    if (object instanceof CacheStats) {
      CacheStats other = (CacheStats) object;
      return hitCount == other.hitCount
          && missCount == other.missCount
          && loadSuccessCount == other.loadSuccessCount
          && loadExceptionCount == other.loadExceptionCount
          && totalLoadTime == other.totalLoadTime
          && evictionCount == other.evictionCount;
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("hitCount", hitCount)
        .add("missCount", missCount)
        .add("loadSuccessCount", loadSuccessCount)
        .add("loadExceptionCount", loadExceptionCount)
        .add("totalLoadTime", totalLoadTime)
        .add("evictionCount", evictionCount)
        .toString();
  }
}
