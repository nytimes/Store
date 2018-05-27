package com.nytimes.android.external.store3.base.room;

import com.nytimes.android.external.store3.annotations.Experimental;
import com.nytimes.android.external.store3.base.BasePersister;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

/**
 * Interface for fetching data from persister
 * when implementing also think about implementing PathResolver to ease in creating primary keys
 *
 * @param <Raw> data type before parsing
 */
@Experimental
public interface RoomPersister<Raw, Parsed, Key> extends
        RoomDiskRead<Parsed, Key>, RoomDiskWrite<Raw, Key>, BasePersister {

    /**
     * @param key to use to get data from persister
     *            If data is not available implementer needs to
     *            either return Observable.empty or throw an exception
     */
    @Override
    @Nonnull
    Observable<Parsed> read(@Nonnull final Key key);

    /**
     * @param key to use to store data to persister
     * @param raw raw string to be stored
     */
    @Override
    @Nonnull
    void write(@Nonnull final Key key, @Nonnull final Raw raw);
}
