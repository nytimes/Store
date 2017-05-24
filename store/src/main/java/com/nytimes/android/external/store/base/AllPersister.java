package com.nytimes.android.external.store.base;


import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import rx.Observable;

public interface AllPersister<Raw> {
    /**
     * @param path to use to get data from persister
     *                If data is not available implementer needs to
     *                throw an exception
     */
    @Nonnull
    Observable<Raw> readAll(@Nonnull final String path) throws FileNotFoundException;

    /**
     * @param path to delete all the date in the the path.
     */
    @Nonnull
    Observable<Boolean> deleteAll(@Nonnull final String path);
}
