package com.nytimes.android.external.store.base;

import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import rx.Observable;

public interface DiskAllRead<Raw> {
    @Nonnull
    Observable<Raw> readAll(@Nonnull String path) throws FileNotFoundException;
}
