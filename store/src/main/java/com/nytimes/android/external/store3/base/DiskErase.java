package com.nytimes.android.external.store3.base;


import javax.annotation.Nonnull;

import io.reactivex.Observable;

public interface DiskErase<Raw, Key> {
    /**
     * @param key to use to delete a particular file using persister
     */
    @Nonnull
    Observable<Boolean> delete(@Nonnull Key key);
}
