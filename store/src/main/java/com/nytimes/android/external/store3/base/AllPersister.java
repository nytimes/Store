package com.nytimes.android.external.store3.base;


import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;


public interface AllPersister<Raw, Key> extends Persister<Raw, Key>, DiskAllRead<Raw>, DiskAllErase {

    /**
     * @param path to use to get data from persister
     *                If data is not available implementer needs to
     *                check for failures in the emitted {@link ReadResult}
     */
    @Nonnull
    @Override
    Observable<ReadResult<Raw>> safeReadAll(@Nonnull final String path);

    /**
     * @deprecated Use {@link #safeReadAll(String)} instead
     * @param path to use to get data from persister
     *                If data is not available implementer needs to
     *                throw an exception
     */
    @Nonnull
    @Override
    Observable<Raw> readAll(@Nonnull String path) throws FileNotFoundException;

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
    Maybe<Raw> read(@Nonnull final Key key);

    /**
     * @param key to use to store data to persister
     * @param raw     raw string to be stored
     */
    @Override
    @Nonnull
    Single<Boolean> write(@Nonnull final Key key, @Nonnull final Raw raw);
}
