package com.nytimes.android.sample.cache;

import android.support.annotation.Nullable;

public interface AsyncFunction<I, O> {
  ListenableFuture<O> apply(@Nullable I var1) throws Exception;
}
