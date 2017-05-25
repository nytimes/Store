package com.nytimes.android.external.store.base;


import java.io.FileNotFoundException;


import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import rx.Observable;

public interface AllPersister<Raw, Key> extends Persister<Raw, Key>, DiskAllRead, DiskAllErase {
    /**
     * @param path to use to get data from persister
     *                If data is not available implementer needs to
     *                throw an exception
     */
    @Override
    @Nonnull
    Observable<Raw> readAll(@Nonnull final String path) throws FileNotFoundException;

    /**
     * @param path to delete all the data in the the path.
     */
    @Override
    @Nonnull
    Observable<Boolean> deleteAll(@Nonnull final String path);

    /**
     * @param key to use to get data from persister
     *                If data is not available implementer needs to
     *                throw an exception
     */
    @Override
    @Nonnull
    Observable<Raw> read(@Nonnull final Key key);

    /**
     * @param key to use to store data to persister
     * @param raw     raw string to be stored
     */
    @Override
    @Nonnull
    Observable<Boolean> write(@Nonnull final Key key, @Nonnull final Raw raw);
}
