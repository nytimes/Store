package com.nytimes.android.external.store3.base.room;

import com.nytimes.android.external.store3.annotations.Experimental;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

    @Experimental
public interface RoomDiskRead<Raw, Key> {
    @Nonnull
    Observable<Raw> read(@Nonnull Key key);
}
