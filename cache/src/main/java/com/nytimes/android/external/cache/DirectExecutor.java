package com.nytimes.android.external.cache;

import java.util.concurrent.Executor;

enum DirectExecutor implements Executor {
    INSTANCE;
    @Override public void execute(Runnable command) {
      command.run();
    }

    @Override public String toString() {
      return "MoreExecutors.directExecutor()";
    }
  }
