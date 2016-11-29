package com.nytimes.android.store.base;

import com.nytimes.android.store.base.impl.BarCode;

import rx.Observable;

public interface DiskRead<Raw> {
    Observable<Raw> read(BarCode barCode);
}
