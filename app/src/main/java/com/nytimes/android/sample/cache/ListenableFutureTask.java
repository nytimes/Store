package com.nytimes.android.sample.cache;


import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class ListenableFutureTask<V> extends FutureTask<V> implements ListenableFuture<V> {
    private final ExecutionList executionList = new ExecutionList();

    public static <V> ListenableFutureTask<V> create(Callable<V> callable) {
        return new ListenableFutureTask(callable);
    }



    ListenableFutureTask(Callable<V> callable) {
        super(callable);
    }


    public void addListener(Runnable listener, Executor exec) {
        this.executionList.add(listener, exec);
    }

    protected void done() {
        this.executionList.execute();
    }
}
