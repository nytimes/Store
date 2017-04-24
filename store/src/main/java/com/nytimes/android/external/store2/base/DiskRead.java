package com.nytimes.android.external.store2.base;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;

public interface DiskRead<Raw, Key> {
    @Nonnull
    Maybe<Raw> read(@Nonnull Key key);
}
