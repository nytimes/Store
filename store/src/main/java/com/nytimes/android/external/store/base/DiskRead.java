package com.nytimes.android.external.store.base;

import javax.annotation.Nonnull;

import rx.Observable;

public interface DiskRead<Raw, Key> {
    @Nonnull
    Observable<Raw> read(Key key);
}
