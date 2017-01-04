package com.nytimes.android.external.cache;


import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Futures {
    private Futures() {
    }


    public static <V> ListenableFuture<V> immediateFuture(  V value) {
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

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(function);
        Futures.ChainingFuture output = new Futures.ChainingFuture(input, function);
        input.addListener(output, DirectExecutor.INSTANCE);
        return output;
    }

    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws X {
        return FuturesGetChecked.getChecked(future, exceptionClass);
    }

    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws X {
        return FuturesGetChecked.getChecked(future, exceptionClass, timeout, unit);
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

        ListenableFuture<? extends I> inputFuture;

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

        private final V value;

        ImmediateSuccessfulFuture(  V value) {
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
}

