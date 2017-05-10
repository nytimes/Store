package com.nytimes.android.external.store.base;

import javax.annotation.Nonnull;

import rx.Observable;

public interface DiskErase<Raw, Key> {
    /**
     * @param key to use to delete a particular file using persister
     */
    @Nonnull
    Observable<Boolean> delete(@Nonnull Key key);

    /**
     * @param key to use to delete all data data using persister
     */
    @Nonnull
    Observable<Boolean> deleteAll(@Nonnull Key key);
}
