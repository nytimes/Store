package com.nytimes.android.external.store3.base;


import javax.annotation.Nonnull;

import io.reactivex.Observable;

public interface DiskAllErase {
    /**
     * @param path to use to delete all files
     */
    @Nonnull
    Observable<Boolean> deleteAll(@Nonnull String path);
}
