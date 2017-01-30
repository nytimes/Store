package com.nytimes.android.external.cache;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

enum DirectExecutor implements Executor {
    INSTANCE;
    @Override public void execute(@NotNull Runnable command) {
      command.run();
    }

    @Override public String toString() {
      return "MoreExecutors.directExecutor()";
    }
  }
