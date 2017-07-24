package com.nytimes.android.external.store3.base;


import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

public interface DiskAllRead<Raw> {
    @Nonnull
    Observable<Raw> readAll(@Nonnull String path) throws FileNotFoundException;
}

