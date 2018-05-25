package com.nytimes.android.external.store3.base.room;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

public interface RoomDiskRead<Raw, Key> {
    @Nonnull
    Observable<Raw> read(@Nonnull Key key);
}
