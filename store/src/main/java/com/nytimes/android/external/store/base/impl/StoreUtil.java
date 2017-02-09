package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.RecordProvider;
import com.nytimes.android.external.store.base.RecordState;

import java.util.Objects;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import static com.nytimes.android.external.store.base.RecordState.FRESH;

final class StoreUtil {
    private StoreUtil() {
    }

    @Nonnull
    static <Parsed, Key> Observable.Transformer<Parsed, Parsed>
    repeatWhenCacheEvicted(PublishSubject<Key> refreshSubject, @Nonnull final Key key) {
        Observable<Key> filter = refreshSubject.filter(new Func1<Key, Boolean>() {
            @Override
            public Boolean call(Key key) {
                return key.equals(key);
            }
        });
        return from(filter);
    }

    @Nonnull
    static <T> Observable.Transformer<T, T> from(@Nonnull final Observable retrySource) {
        Objects.requireNonNull(retrySource);
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> source) {
                return source.repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> events) {
                        return events.switchMap(new Func1<Void, Observable<?>>() {
                            @Override
                            public Observable<?> call(Void aVoid) {
                                return retrySource;
                            }
                        });
                    }
                });
            }
        };
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
            return recordState != FRESH;
        }
        return false;
    }
}
