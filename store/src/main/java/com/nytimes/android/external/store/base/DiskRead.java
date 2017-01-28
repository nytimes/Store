package com.nytimes.android.external.store.base;

import com.nytimes.android.external.store.base.impl.BarCode;

import org.jetbrains.annotations.NotNull;

import rx.Observable;

public interface DiskRead<Raw> {
    @NotNull
    Observable<Raw> read(BarCode barCode);
}
