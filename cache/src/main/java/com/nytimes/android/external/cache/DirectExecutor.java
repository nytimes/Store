package com.nytimes.android.external.cache;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

enum DirectExecutor implements Executor {
    INSTANCE;
    @Override public void execute(@NonNull Runnable command) {
      command.run();
    }

    @Override public String toString() {
      return "MoreExecutors.directExecutor()";
    }
  }
