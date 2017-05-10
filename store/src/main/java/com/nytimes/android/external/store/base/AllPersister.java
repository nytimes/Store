package com.nytimes.android.external.store.base;


import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import rx.Observable;

public interface AllPersister<Raw, Key> {
    /**
     * @param key to use to get data from persister
     *                If data is not available implementer needs to
     *                throw an exception
     */
    @Nonnull
    Observable<Raw> readAll(@Nonnull final Key key) throws FileNotFoundException;

    /**
     * @param key to use to delete all data to persister
     */
    @Nonnull
    Observable<Boolean> deleteAll(@Nonnull final Key key);
}
