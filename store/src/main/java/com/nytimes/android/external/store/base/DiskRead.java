package com.nytimes.android.external.store.base;

import android.support.annotation.NonNull;

import com.nytimes.android.external.store.base.impl.BarCode;

import rx.Observable;

public interface DiskRead<Raw> {
    @NonNull
    Observable<Raw> read(BarCode barCode);
}
