package com.nytimes.android.external.store3.base;


import javax.annotation.Nonnull;

import io.reactivex.Single;

public interface DiskErase<Key> {
    /**
     * @param key to use to delete a particular file using persister
     */
    @Nonnull
    Single<Boolean> delete(@Nonnull Key key);
}
