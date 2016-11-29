package com.nytimes.android.external.store.base;

import com.nytimes.android.external.store.base.impl.BarCode;

import rx.Observable;

public interface DiskRead<Raw> {
    Observable<Raw> read(BarCode barCode);
}
