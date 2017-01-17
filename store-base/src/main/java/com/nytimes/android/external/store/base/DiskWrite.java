package com.nytimes.android.external.store.base;

import org.jetbrains.annotations.NotNull;

import rx.Observable;

public interface DiskWrite<Raw> {
    /**
     * @param barCode to use to get data from persister
     *                If data is not available implementer needs to
     *                either return Observable.empty or throw an exception
     */
    @NotNull
    Observable<Boolean> write(BarCode barCode, Raw raw);
}
