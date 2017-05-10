package com.nytimes.android.external.store.base;

import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import rx.Observable;

public interface DiskRead<Raw, Key> {
    @Nonnull
    Observable<Raw> read(@Nonnull Key key);

    @Nonnull
    Observable<Raw> readAll(@Nonnull Key key) throws FileNotFoundException;
}
