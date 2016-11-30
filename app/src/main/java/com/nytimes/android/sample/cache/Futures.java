package com.nytimes.android.sample.cache;

import android.support.annotation.Nullable;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Futures {
// --Commented out by Inspection START (11/29/16, 5:03 PM):
//    private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction() {
//        @Override
//        public ListenableFuture apply(@Nullable Object var1) throws Exception {
//            return null;
//        }
//    };
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)

    private Futures() {
    }


    public static <V> ListenableFuture<V> immediateFuture(@Nullable V value) {
        if (value == null) {
            Futures.ImmediateSuccessfulFuture typedNull = Futures.ImmediateSuccessfulFuture.NULL;
            return typedNull;
        } else {
            return new Futures.ImmediateSuccessfulFuture(value);
        }
    }


    public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        return new Futures.ImmediateFailedFuture(throwable);
    }


// --Commented out by Inspection START (11/29/16, 5:03 PM):
//    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback, Executor executor) {
//        Futures.AsyncCatchingFuture future = new Futures.AsyncCatchingFuture(input, exceptionType, fallback);
//        input.addListener(future, rejectionPropagatingExecutor(executor, future));
//        return future;
//    }
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)


    private static Executor rejectionPropagatingExecutor(final Executor delegate, final AbstractFuture<?> future) {
        Preconditions.checkNotNull(delegate);
        return delegate == DirectExecutor.INSTANCE ? delegate : new Executor() {
            volatile boolean thrownFromDelegate = true;

            public void execute(final Runnable command) {
                try {
                    delegate.execute(new Runnable() {
                        public void run() {
                            thrownFromDelegate = false;
                            command.run();
                        }
                    });
                } catch (RejectedExecutionException var3) {
                    if (this.thrownFromDelegate) {
                        future.setException(var3);
                    }
                }

            }
        };
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(function);
        Futures.ChainingFuture output = new Futures.ChainingFuture(input, function);
        input.addListener(output, DirectExecutor.INSTANCE);
        return output;
    }

// --Commented out by Inspection START (11/29/16, 5:03 PM):
//    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor) {
//        Preconditions.checkNotNull(function);
//        Futures.ChainingFuture output = new Futures.ChainingFuture(input, function);
//        input.addListener(output, rejectionPropagatingExecutor(executor, output));
//        return output;
//    }
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)


// --Commented out by Inspection START (11/29/16, 5:03 PM):
//    /**
//     * @deprecated
//     */
//    @Deprecated
//    public static <V, X extends Exception> V get(Future<V> future, Class<X> exceptionClass) throws X {
//        return getChecked(future, exceptionClass);
//    }
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)

// --Commented out by Inspection START (11/29/16, 5:03 PM):
//    /**
//     * @deprecated
//     */
//    @Deprecated
//    public static <V, X extends Exception> V get(Future<V> future, long timeout, TimeUnit unit, Class<X> exceptionClass) throws X {
//        return getChecked(future, exceptionClass, timeout, unit);
//    }
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)

    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws X {
        return FuturesGetChecked.getChecked(future, exceptionClass);
    }

    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws X {
        return FuturesGetChecked.getChecked(future, exceptionClass, timeout, unit);
    }


// --Commented out by Inspection START (11/29/16, 5:03 PM):
//    public static <V> V getUnchecked(Future<V> future) {
//        Preconditions.checkNotNull(future);
//
//        try {
//            return Uninterruptibles.getUninterruptibly(future);
//        } catch (ExecutionException var2) {
//            wrapAndThrowUnchecked(var2.getCause());
//            throw new AssertionError();
//        }
//    }
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)


    private static void wrapAndThrowUnchecked(Throwable cause) {
        if (cause instanceof Error) {
            throw new ExecutionError((Error) cause);
        } else {
            throw new UncheckedExecutionException(cause);
        }
    }


    private static final class ChainingFuture<I, O> extends Futures.AbstractChainingFuture<I, O, Function<? super I, ? extends O>> {
        ChainingFuture(ListenableFuture<? extends I> inputFuture, Function<? super I, ? extends O> function) {
            super(inputFuture, function);
        }

        void doTransform(Function<? super I, ? extends O> function, I input) {
            this.set(function.apply(input));
        }
    }


    private abstract static class AbstractChainingFuture<I, O, F>
            extends AbstractFuture.TrustedFuture<O> implements Runnable {
        // In theory, this field might not be visible to a cancel() call in certain circumstances. For
        // details, see the comments on the fields of TimeoutFuture.
        @Nullable
        ListenableFuture<? extends I> inputFuture;
        @Nullable
        F function;

        AbstractChainingFuture(ListenableFuture<? extends I> inputFuture, F function) {
            this.inputFuture = Preconditions.checkNotNull(inputFuture);
            this.function = Preconditions.checkNotNull(function);
        }

        @Override
        public final void run() {
            try {
                ListenableFuture<? extends I> localInputFuture = inputFuture;
                F localFunction = function;
                if (isCancelled() | localInputFuture == null | localFunction == null) {
                    return;
                }
                inputFuture = null;
                function = null;

                I sourceResult;
                try {
                    sourceResult = Uninterruptibles.getUninterruptibly(localInputFuture);
                } catch (CancellationException e) {
                    // Cancel this future and return.
                    // At this point, inputFuture is cancelled and outputFuture doesn't
                    // exist, so the value of mayInterruptIfRunning is irrelevant.
                    cancel(false);
                    return;
                } catch (ExecutionException e) {
                    // Set the cause of the exception as this future's exception
                    setException(e.getCause());
                    return;
                }
                doTransform(localFunction, sourceResult);
            } catch (UndeclaredThrowableException e) {
                // Set the cause of the exception as this future's exception
                setException(e.getCause());
            } catch (Throwable t) {
                // This exception is irrelevant in this thread, but useful for the
                // client
                setException(t);
            }
        }

        abstract void doTransform(F function, I result) throws Exception;


        final void done() {
            this.maybePropagateCancellation(this.inputFuture);
            this.inputFuture = null;
        }
    }


    private static class ImmediateFailedFuture<V> extends Futures.ImmediateFuture<V> {
        private final Throwable thrown;

        ImmediateFailedFuture(Throwable thrown) {
            super();
            this.thrown = thrown;
        }

        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }


    private static class ImmediateSuccessfulFuture<V> extends Futures.ImmediateFuture<V> {
        static final Futures.ImmediateSuccessfulFuture<Object> NULL = new Futures.ImmediateSuccessfulFuture((Object) null);
        @Nullable
        private final V value;

        ImmediateSuccessfulFuture(@Nullable V value) {
            super();
            this.value = value;
        }

        public V get() {
            return this.value;
        }
    }

    private abstract static class ImmediateFuture<V> implements ListenableFuture<V> {
        private static final Logger log = Logger.getLogger(Futures.ImmediateFuture.class.getName());

        private ImmediateFuture() {
        }

        public void addListener(Runnable listener, Executor executor) {
            Preconditions.checkNotNull(listener, "Runnable was null.");
            Preconditions.checkNotNull(executor, "Executor was null.");

            try {
                executor.execute(listener);
            } catch (RuntimeException var4) {
                log.log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, var4);
            }

        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        public abstract V get() throws ExecutionException;

        public V get(long timeout, TimeUnit unit) throws ExecutionException {
            Preconditions.checkNotNull(unit);
            return this.get();
        }

        public boolean isCancelled() {
            return false;
        }

        public boolean isDone() {
            return true;
        }
    }

    static final class AsyncCatchingFuture<V, X extends Throwable>
            extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>> {

        AsyncCatchingFuture(ListenableFuture<? extends V> input, Class<X> exceptionType,
                            AsyncFunction<? super X, ? extends V> fallback) {
            super(input, exceptionType, fallback);
        }


        @Override
        void doFallback(
                AsyncFunction<? super X, ? extends V> fallback, X cause) throws Exception {
            ListenableFuture<? extends V> replacement = fallback.apply(cause);
            Preconditions.checkNotNull(replacement, "AsyncFunction.apply returned null instead of a Future. "
                    + "Did you mean to return immediateFuture(null)?");
            setFuture(replacement);
        }


    }

    private abstract static class AbstractCatchingFuture<V, X extends Throwable, F>
            extends AbstractFuture.TrustedFuture<V> implements Runnable {
        @Nullable
        ListenableFuture<? extends V> inputFuture;
        @Nullable
        Class<X> exceptionType;
        @Nullable
        F fallback;

        AbstractCatchingFuture(
                ListenableFuture<? extends V> inputFuture, Class<X> exceptionType, F fallback) {
            this.inputFuture = Preconditions.checkNotNull(inputFuture);
            this.exceptionType = Preconditions.checkNotNull(exceptionType);
            this.fallback = Preconditions.checkNotNull(fallback);
        }

        @Override
        public final void run() {
            ListenableFuture<? extends V> localInputFuture = inputFuture;
            Class<X> localExceptionType = exceptionType;
            F localFallback = fallback;
            if (localInputFuture == null | localExceptionType == null | localFallback == null
                    | isCancelled()) {
                return;
            }
            inputFuture = null;
            exceptionType = null;
            fallback = null;

            Throwable throwable;
            try {
                set(Uninterruptibles.getUninterruptibly(localInputFuture));
                return;
            } catch (ExecutionException e) {
                throwable = e.getCause();
            } catch (Throwable e) {  // this includes cancellation exception
                throwable = e;
            }
            try {
                if (isInstanceOfThrowableClass(throwable, localExceptionType)) {
                    @SuppressWarnings("unchecked") // verified safe by isInstance
                            X castThrowable = (X) throwable;
                    doFallback(localFallback, castThrowable);
                } else {
                    setException(throwable);
                }
            } catch (Throwable e) {
                setException(e);
            }
        }

        /**
         * Template method for subtypes to actually run the fallback.
         */
        abstract void doFallback(F fallback, X throwable) throws Exception;

        @Override
        final void done() {
            maybePropagateCancellation(inputFuture);
            this.inputFuture = null;
            this.exceptionType = null;
            this.fallback = null;
        }
    }

    static boolean isInstanceOfThrowableClass(
            @Nullable Throwable t, Class<? extends Throwable> expectedClass) {
        return expectedClass.isInstance(t);
    }
}

