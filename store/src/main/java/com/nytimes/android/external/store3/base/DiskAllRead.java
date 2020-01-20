package com.nytimes.android.external.store3.base;


import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

public interface DiskAllRead<Raw> {

    /**
     * Use {@link #safeReadAll(String)} instead
     */
    @Deprecated
    @Nonnull
    Observable<Raw> readAll(@Nonnull String path) throws FileNotFoundException;

    @Nonnull
    Observable<ReadResult<Raw>> safeReadAll(@Nonnull final String path);
}

