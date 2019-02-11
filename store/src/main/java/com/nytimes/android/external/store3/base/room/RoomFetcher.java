package com.nytimes.android.external.store3.base.room;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * Interface for fetching new data for a Store
 *
 * @param <Raw> data type before parsing
 */
public interface RoomFetcher<Raw, Key> {

    /**
     * @param key Container with Key and Type used as a request param
     * @return Observable that emits {@link Raw} data
     */
    @Nonnull
    Observable<Raw> fetch(@Nonnull Key key);
}
