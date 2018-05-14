package com.nytimes.android.external.store3.base.impl;

import com.nytimes.android.external.store3.base.Clearable;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.RecordProvider;
import com.nytimes.android.external.store3.base.RecordState;

import javax.annotation.Nonnull;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.PublishSubject;

import static com.nytimes.android.external.store3.base.RecordState.STALE;

final class StoreUtil {
    private StoreUtil() {
    }

    @Nonnull
    static <Parsed, Key> ObservableTransformer<Parsed, Parsed>
    repeatWhenSubjectEmits(PublishSubject<Key> refreshSubject, @Nonnull final Key keyForRepeat) {
        Observable<Key> filter = refreshSubject.filter(key -> key.equals(keyForRepeat));
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

    static <Raw, Key> Completable clearPersister(Persister<Raw, Key> persister, @Nonnull Key key) {
        boolean isPersisterClearable = persister instanceof Clearable;

        if (isPersisterClearable) {
            return ((Clearable<Key>) persister).clear(key);
        } else {
            return Completable.complete();
        }
    }
}
