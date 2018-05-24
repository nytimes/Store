package com.nytimes.android.external.store3.base;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface RoomDiskRead<Raw, Key> {
    @Nonnull
    Observable<Raw> read(@Nonnull Key key);
}
