package com.nytimes.android.external.store.base;

import org.jetbrains.annotations.NotNull;

import com.nytimes.android.external.store.base.impl.BarCode;

import rx.Observable;

public interface DiskRead<Raw> {
    @NotNull
    Observable<Raw> read(BarCode barCode);
}
