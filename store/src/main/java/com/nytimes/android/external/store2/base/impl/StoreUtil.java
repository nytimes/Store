package com.nytimes.android.external.store2.base.impl;

import com.nytimes.android.external.store2.base.Persister;
import com.nytimes.android.external.store2.base.RecordProvider;
import com.nytimes.android.external.store2.base.RecordState;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


import static com.nytimes.android.external.store2.base.RecordState.STALE;

final class StoreUtil {
    private StoreUtil() {
    }

    @Nonnull
    static <Parsed, Key> ObservableTransformer<Parsed, Parsed>
    repeatWhenCacheEvicted(PublishSubject<Key> refreshSubject, @Nonnull final Key keyForRepeat) {
        Observable<Key> filter = refreshSubject.filter(new Predicate<Key>() {
            @Override
            public boolean test(@NonNull Key key) throws Exception {
                return key.equals(keyForRepeat);
            }
        });
        return RepeatWhenEmits.from(filter);
    }

    static <Raw, Key> boolean shouldReturnNetworkBeforeStale(
            Persister<Raw, Key> persister, StalePolicy stalePolicy, Key key) {
        return stalePolicy == StalePolicy.NETWORK_BEFORE_STALE
                && persisterIsStale(key, persister);
    }

    static <Raw, Key> boolean persisterIsStale(@Nonnull Key key, Persister<Raw, Key> persister) {
        if (persister instanceof RecordProvider) {
            RecordProvider<Key> provider = (RecordProvider<Key>) persister;
            RecordState recordState = provider.getRecordState(key);
            return recordState == STALE;
        }
        return false;
    }
}
