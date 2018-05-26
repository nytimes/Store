package com.nytimes.android.external.store3.base.room;

import com.nytimes.android.external.store3.annotations.Experimental;

import javax.annotation.Nonnull;

@Experimental
public interface RoomDiskWrite<Raw, Key> {
    /**
     * @param key to use to get data from persister
     *            If data is not available implementer needs to
     *            either return Observable.empty or throw an exception
     */
    @Nonnull
    void write(@Nonnull Key key, @Nonnull Raw raw);
}
