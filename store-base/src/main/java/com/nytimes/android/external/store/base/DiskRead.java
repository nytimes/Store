package com.nytimes.android.external.store.base;

import android.support.annotation.NonNull;

import rx.Observable;

public interface DiskRead<Raw> {
    @NonNull
    Observable<Raw> read(BaseBarcode barCode);
}
