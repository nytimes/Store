package com.nytimes.android.external.store.base;

import javax.annotation.Nonnull;

import rx.Observable;

public interface DiskAllErase{
    /**
     * @param path to use to delete all files
     */
    @Nonnull
    Observable<Boolean> deleteAll(@Nonnull String path);
}
