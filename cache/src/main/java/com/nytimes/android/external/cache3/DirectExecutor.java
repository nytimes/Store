package com.nytimes.android.external.cache3;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

enum DirectExecutor implements Executor {
    INSTANCE;
    @Override public void execute(@Nonnull Runnable command) {
      command.run();
    }

    @Override public String toString() {
      return "MoreExecutors.directExecutor()";
    }
  }
