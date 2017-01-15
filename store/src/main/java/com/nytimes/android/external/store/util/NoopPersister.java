package com.nytimes.android.external.store.util;

import android.support.annotation.NonNull;

import com.nytimes.android.external.store.base.IBarCode;
import com.nytimes.android.external.store.base.Persister;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;

/**
 * Pass-through diskdao for stores that don't want to use persister
 */
public class NoopPersister<Raw> implements Persister<Raw> {
    private final ConcurrentMap<IBarCode, Raw> networkResponses = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public Observable<Raw> read(IBarCode IBarCode) {
        Raw raw = networkResponses.get(IBarCode);
        return raw == null ? Observable.<Raw>empty() : Observable.just(raw);
    }

    @NonNull
    @Override
    public Observable<Boolean> write(IBarCode IBarCode, Raw raw) {
        networkResponses.put(IBarCode, raw);
        return Observable.just(true);
    }
}
