package com.nytimes.android.external.store3.base;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Interface for fetching data from persister
 * when implementing also think about implementing PathResolver to ease in creating primary keys
 *
 * @param <Raw> data type before parsing
 */
public interface RoomPersister< Raw, Parsed,Key> extends RoomDiskRead<Parsed, Key>, RoomDiskWrite<Raw, Key> {

    /**
     * @param key to use to get data from persister
     *                If data is not available implementer needs to
     *                either return Observable.empty or throw an exception
     */
    @Override
    @Nonnull
    Observable<Parsed> read(@Nonnull final Key key);

    /**
     * @param key to use to store data to persister
     * @param raw     raw string to be stored
     */
    @Override
    @Nonnull
    void write(@Nonnull final Key key, @Nonnull final Raw raw);
}
