package com.nytimes.android.external.store.base;

import com.nytimes.android.external.store.base.impl.BarCode;

import javax.annotation.Nonnull;

import rx.Observable;

public interface DiskRead<Raw> {
    @Nonnull
    Observable<Raw> read(BarCode barCode);
}
