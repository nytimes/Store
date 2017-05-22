/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nytimes.android.external.cache3;


import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import static com.nytimes.android.external.cache3.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * An object that measures elapsed time in nanoseconds. It is useful to measure
 * elapsed time using this class instead of direct calls to {@link
 * System#nanoTime} for a few reasons:
 * <p>
 * <ul>
 * <li>An alternate time source can be substituted, for testing or performance
 * reasons.
 * <li>As documented by {@code nanoTime}, the value returned has no absolute
 * meaning, and can only be interpreted as relative to another timestamp
 * returned by {@code nanoTime} at a different time. {@code Stopwatch} is a
 * more effective abstraction because it exposes only these relative values,
 * not the absolute ones.
 * </ul>
 * <p>
 * <p>Basic usage:
 * <pre>
 *   Stopwatch stopwatch = Stopwatch.{@link #createStarted createStarted}();
 *   doSomething();
 *   stopwatch.{@link #stop stop}(); // optional
 *
 *   long millis = stopwatch.elapsed(MILLISECONDS);
 *
 *   log.info("time: " + stopwatch); // formatted string like "12.3 ms"</pre>
 * <p>
 * <p>Stopwatch methods are not idempotent; it is an error to start or stop a
 * stopwatch that is already in the desired state.
 * <p>
 * <p>When testing code that uses this class, use
 * {@link #createUnstarted(Ticker)} or {@link #createStarted(Ticker)} to
 * supply a fake or mock ticker.
 * <!-- TODO(kevinb): restore the "such as" --> This allows you to
 * simulate any valid behavior of the stopwatch.
 * <p>
 * <p><b>Note:</b> This class is not thread-safe.
 *
 * @author Kevin Bourrillion
 * @since 10.0
 */
public final class Stopwatch {
    @Nonnull
    private final Ticker ticker;
    private boolean isRunning;
    private long elapsedNanos;
    private long startTick;

    /**
     * Creates (but does not start) a new stopwatch using {@link System#nanoTime}
     * as its time source.
     *
     * @since 15.0
     */
    @Nonnull
    public static Stopwatch createUnstarted() {
        return new Stopwatch();
    }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Creates (but does not start) a new stopwatch, using the specified time
//   * source.
//   *
//   * @since 15.0
//   */
//  public static Stopwatch createUnstarted(Ticker ticker) {
//    return new Stopwatch(ticker);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

    /**
     * Creates (and starts) a new stopwatch using {@link System#nanoTime}
     * as its time source.
     *
     * @since 15.0
     */
    @Nonnull
    public static Stopwatch createStarted() {
        return new Stopwatch().start();
    }


    Stopwatch() {
        this.ticker = Ticker.systemTicker();
    }


    /**
     * Starts the stopwatch.
     *
     * @return this {@code Stopwatch} instance
     * @throws IllegalStateException if the stopwatch is already running.
     */
    @Nonnull
    public Stopwatch start() {
        checkState(!isRunning, "This stopwatch is already running.");
        isRunning = true;
        startTick = ticker.read();
        return this;
    }

    /**
     * Stops the stopwatch. Future reads will return the fixed duration that had
     * elapsed up to this point.
     *
     * @return this {@code Stopwatch} instance
     * @throws IllegalStateException if the stopwatch is already stopped.
     */
    @Nonnull
    public Stopwatch stop() {
        long tick = ticker.read();
        checkState(isRunning, "This stopwatch is already stopped.");
        isRunning = false;
        elapsedNanos += tick - startTick;
        return this;
    }


    private long elapsedNanos() {
        return isRunning ? ticker.read() - startTick + elapsedNanos : elapsedNanos;
    }


    /**
     * Returns a string representation of the current elapsed time.
     */
    @Override
    public String toString() {
        long nanos = elapsedNanos();

        TimeUnit unit = chooseUnit(nanos);
        double value = (double) nanos / NANOSECONDS.convert(1, unit);

        // Too bad this functionality is not exposed as a regular method call
        return String.format(Locale.ROOT, "%.4g %s", value, abbreviate(unit));
    }

    @Nonnull
    private static TimeUnit chooseUnit(long nanos) {
        if (DAYS.convert(nanos, NANOSECONDS) > 0) {
            return DAYS;
        }
        if (HOURS.convert(nanos, NANOSECONDS) > 0) {
            return HOURS;
        }
        if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
            return MINUTES;
        }
        if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
            return SECONDS;
        }
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MILLISECONDS;
        }
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MICROSECONDS;
        }
        return NANOSECONDS;
    }

    private static String abbreviate(@Nonnull TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "\u03bcs"; // μs
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new AssertionError();
        }
    }
}
