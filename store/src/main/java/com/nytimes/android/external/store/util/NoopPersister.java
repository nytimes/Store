package com.nytimes.android.external.store.util;


import com.nytimes.android.external.store.base.Clearable;
import com.nytimes.android.external.store.base.Persister;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * Pass-through diskdao for stores that don't want to use persister
 */
public class NoopPersister<Raw, Key> implements Persister<Raw, Key>, Clearable<Key> {
    protected final ConcurrentMap<Key, Raw> networkResponses = new ConcurrentHashMap<>();

    @Nonnull
    @Override
    public Observable<Raw> read(Key barCode) {
        Raw raw = networkResponses.get(barCode);
        return raw == null ? Observable.<Raw>empty() : Observable.just(raw);
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(Key barCode, Raw raw) {
        networkResponses.put(barCode, raw);
        return Observable.just(true);
    }

    @Override
    public void clear(Key key) {
        networkResponses.remove(key);
    }
}
