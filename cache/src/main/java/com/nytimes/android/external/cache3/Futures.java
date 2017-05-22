package com.nytimes.android.external.cache3;



import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Futures {
    private Futures() {
    }


    @Nullable
    public static <V> com.nytimes.android.external.cache3.ListenableFuture<V> immediateFuture(@Nullable V value) {
        if (value == null) {
            //noinspection unchecked safe because of erasure
            return (com.nytimes.android.external.cache3.ListenableFuture<V>) ImmediateSuccessfulFuture.NULL;
        } else {
            return new Futures.ImmediateSuccessfulFuture<>(value);
        }
    }


    @Nonnull
    public static <V> com.nytimes.android.external.cache3.ListenableFuture<V> immediateFailedFuture(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        return new Futures.ImmediateFailedFuture<>(throwable);
    }

    @Nonnull
    public static <I, O> com.nytimes.android.external.cache3.ListenableFuture<O> transform(@Nonnull com.nytimes.android.external.cache3.ListenableFuture<I> input, com.nytimes.android.external.cache3.Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(function);
        Futures.ChainingFuture<I,O> output = new Futures.ChainingFuture(input, function);
        input.addListener(output, DirectExecutor.INSTANCE);
        return output;
    }

    public static <V, X extends Exception> V getChecked(@Nonnull Future<V> future, Class<X> exceptionClass) throws X {
        return com.nytimes.android.external.cache3.FuturesGetChecked.getChecked(future, exceptionClass);
    }

    public static <V, X extends Exception> V getChecked(@Nonnull Future<V> future, Class<X> exceptionClass, long timeout, @Nonnull TimeUnit unit) throws X {
        return com.nytimes.android.external.cache3.FuturesGetChecked.getChecked(future, exceptionClass, timeout, unit);
    }



    private static final class ChainingFuture<I, O> extends Futures.AbstractChainingFuture<I, O, com.nytimes.android.external.cache3.Function<? super I, ? extends O>> {
        ChainingFuture(com.nytimes.android.external.cache3.ListenableFuture<? extends I> inputFuture, com.nytimes.android.external.cache3.Function<? super I, ? extends O> function) {
            super(inputFuture, function);
        }

        @Override
        void doTransform(@Nonnull com.nytimes.android.external.cache3.Function<? super I, ? extends O> function, I input) {
            this.set(function.apply(input));
        }
    }


    private abstract static class AbstractChainingFuture<I, O, F>
            extends com.nytimes.android.external.cache3.AbstractFuture.TrustedFuture<O> implements Runnable {
        // In theory, this field might not be visible to a cancel() call in certain circumstances. For
        // details, see the comments on the fields of TimeoutFuture.

        @Nullable
        com.nytimes.android.external.cache3.ListenableFuture<? extends I> inputFuture;

        @Nullable
        F function;

        AbstractChainingFuture(com.nytimes.android.external.cache3.ListenableFuture<? extends I> inputFuture, F function) {
            this.inputFuture = Preconditions.checkNotNull(inputFuture);
            this.function = Preconditions.checkNotNull(function);
        }

        @Override
        public final void run() {
            try {
                com.nytimes.android.external.cache3.ListenableFuture<? extends I> localInputFuture = inputFuture;
                F localFunction = function;
                if (isCancelled() | localInputFuture == null | localFunction == null) {
                    return;
                }
                inputFuture = null;
                function = null;

                I sourceResult;
                try {
                    sourceResult = com.nytimes.android.external.cache3.Uninterruptibles.getUninterruptibly(localInputFuture);
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


        @Override
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

        @Override @Nonnull
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }


    private static class ImmediateSuccessfulFuture<V> extends Futures.ImmediateFuture<V> {
        @Nullable
        static final Futures.ImmediateSuccessfulFuture<Object> NULL = new Futures.ImmediateSuccessfulFuture<>(null);

        private final V value;

        ImmediateSuccessfulFuture(  V value) {
            super();
            this.value = value;
        }

        @Override
        public V get() {
            return this.value;
        }
    }

    private abstract static class ImmediateFuture<V> implements com.nytimes.android.external.cache3.ListenableFuture<V> {
        private static final Logger log = Logger.getLogger(Futures.ImmediateFuture.class.getName());

        private ImmediateFuture() {
        }

        @Override
        public void addListener(@Nonnull Runnable listener, @Nonnull Executor executor) {
            Preconditions.checkNotNull(listener, "Runnable was null.");
            Preconditions.checkNotNull(executor, "Executor was null.");

            try {
                executor.execute(listener);
            } catch (RuntimeException var4) {
                log.log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, var4);
            }

        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public abstract V get() throws ExecutionException;

        @Override
        public V get(long timeout, @Nonnull TimeUnit unit) throws ExecutionException {
            Preconditions.checkNotNull(unit);
            return this.get();
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }
    }
}

