package com.nytimes.android.sample.cache;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public final class Uninterruptibles {

  // Implementation Note: As of 3-7-11, the logic for each blocking/timeout
  // methods is identical, save for method being invoked.

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes {@code latch.}{@link CountDownLatch#await() await()}
//   * uninterruptibly.
//   */
//  public static void awaitUninterruptibly(CountDownLatch latch) {
//    boolean interrupted = false;
//    try {
//      while (true) {
//        try {
//          latch.await();
//          return;
//        } catch (InterruptedException e) {
//          interrupted = true;
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes
//   * {@code latch.}{@link CountDownLatch#await(long, TimeUnit)
//   * await(timeout, unit)} uninterruptibly.
//   */
//  public static boolean awaitUninterruptibly(CountDownLatch latch,
//      long timeout, TimeUnit unit) {
//    boolean interrupted = false;
//    try {
//      long remainingNanos = unit.toNanos(timeout);
//      long end = System.nanoTime() + remainingNanos;
//
//      while (true) {
//        try {
//          // CountDownLatch treats negative timeouts just like zero.
//          return latch.await(remainingNanos, NANOSECONDS);
//        } catch (InterruptedException e) {
//          interrupted = true;
//          remainingNanos = end - System.nanoTime();
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes {@code toJoin.}{@link Thread#join() join()} uninterruptibly.
//   */
//  public static void joinUninterruptibly(Thread toJoin) {
//    boolean interrupted = false;
//    try {
//      while (true) {
//        try {
//          toJoin.join();
//          return;
//        } catch (InterruptedException e) {
//          interrupted = true;
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

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
  public static <V> V getUninterruptibly(Future<V> future)
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

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes
//   * {@code future.}{@link Future#get(long, TimeUnit) get(timeout, unit)}
//   * uninterruptibly.
//   *
//   * <p>If instead, you wish to treat {@link InterruptedException} uniformly
//   * with other exceptions, see @link Futures#getChecked(Future, Class)
//   * Futures.getChecked}.
//   *
//   * @throws ExecutionException if the computation threw an exception
//   * @throws TimeoutException if the wait timed out
//   */
//  public static <V> V getUninterruptibly(
//      Future<V> future, long timeout, TimeUnit unit)
//          throws ExecutionException, TimeoutException {
//    boolean interrupted = false;
//    try {
//      long remainingNanos = unit.toNanos(timeout);
//      long end = System.nanoTime() + remainingNanos;
//
//      while (true) {
//        try {
//          // Future treats negative timeouts just like zero.
//          return future.get(remainingNanos, NANOSECONDS);
//        } catch (InterruptedException e) {
//          interrupted = true;
//          remainingNanos = end - System.nanoTime();
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes
//   * {@code unit.}{@link TimeUnit#timedJoin(Thread, long)
//   * timedJoin(toJoin, timeout)} uninterruptibly.
//   */
//  public static void joinUninterruptibly(Thread toJoin,
//      long timeout, TimeUnit unit) {
//    Preconditions.checkNotNull(toJoin);
//    boolean interrupted = false;
//    try {
//      long remainingNanos = unit.toNanos(timeout);
//      long end = System.nanoTime() + remainingNanos;
//      while (true) {
//        try {
//          // TimeUnit.timedJoin() treats negative timeouts just like zero.
//          NANOSECONDS.timedJoin(toJoin, remainingNanos);
//          return;
//        } catch (InterruptedException e) {
//          interrupted = true;
//          remainingNanos = end - System.nanoTime();
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes {@code queue.}{@link BlockingQueue#take() take()} uninterruptibly.
//   */
//  public static <E> E takeUninterruptibly(BlockingQueue<E> queue) {
//    boolean interrupted = false;
//    try {
//      while (true) {
//        try {
//          return queue.take();
//        } catch (InterruptedException e) {
//          interrupted = true;
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes {@code queue.}{@link BlockingQueue#put(Object) put(element)}
//   * uninterruptibly.
//   *
//   * @throws ClassCastException if the class of the specified element prevents
//   *     it from being added to the given queue
//   * @throws IllegalArgumentException if some property of the specified element
//   *     prevents it from being added to the given queue
//   */
//  public static <E> void putUninterruptibly(BlockingQueue<E> queue, E element) {
//    boolean interrupted = false;
//    try {
//      while (true) {
//        try {
//          queue.put(element);
//          return;
//        } catch (InterruptedException e) {
//          interrupted = true;
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

  // TODO(user): Support Sleeper somehow (wrapper or interface method)?
// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes {@code unit.}{@link TimeUnit#sleep(long) sleep(sleepFor)}
//   * uninterruptibly.
//   */
//  public static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
//    boolean interrupted = false;
//    try {
//      long remainingNanos = unit.toNanos(sleepFor);
//      long end = System.nanoTime() + remainingNanos;
//      while (true) {
//        try {
//          // TimeUnit.sleep() treats negative timeouts just like zero.
//          NANOSECONDS.sleep(remainingNanos);
//          return;
//        } catch (InterruptedException e) {
//          interrupted = true;
//          remainingNanos = end - System.nanoTime();
//        }
//      }
//    } finally {
//      if (interrupted) {
//        Thread.currentThread().interrupt();
//      }
//    }
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

// --Commented out by Inspection START (11/29/16, 5:05 PM):
//  /**
//   * Invokes {@code semaphore.}{@link Semaphore#tryAcquire(int, long, TimeUnit)
//   * tryAcquire(1, timeout, unit)} uninterruptibly.
//   *
//   * @since 18.0
//   */
//  public static boolean tryAcquireUninterruptibly(
//          Semaphore semaphore, long timeout, TimeUnit unit) {
//    return tryAcquireUninterruptibly(semaphore, 1, timeout, unit);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:05 PM)

  /**
   * Invokes {@code semaphore.}{@link Semaphore#tryAcquire(int, long, TimeUnit)
   * tryAcquire(permits, timeout, unit)} uninterruptibly.
   *
   * @since 18.0
   */
  public static boolean tryAcquireUninterruptibly(
      Semaphore semaphore, int permits, long timeout, TimeUnit unit) {
    boolean interrupted = false;
    try {
      long remainingNanos = unit.toNanos(timeout);
      long end = System.nanoTime() + remainingNanos;

      while (true) {
        try {
          // Semaphore treats negative timeouts just like zero.
          return semaphore.tryAcquire(permits, remainingNanos, NANOSECONDS);
        } catch (InterruptedException e) {
          interrupted = true;
          remainingNanos = end - System.nanoTime();
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }

  // TODO(user): Add support for waitUninterruptibly.

  private Uninterruptibles() {}
}
