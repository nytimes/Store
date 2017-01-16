package com.nytimes.android.external.store.base;

import android.support.annotation.NonNull;

import rx.Observable;

public interface DiskWrite<Raw> {
    /**
     * @param barCode to use to get data from persister
     *                If data is not available implementer needs to
     *                either return Observable.empty or throw an exception
     */
    @NonNull
    Observable<Boolean> write(BaseBarcode barCode, Raw raw);
}
