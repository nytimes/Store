package com.nytimes.android.external.store.util;


import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Resume with empty observable on error
 */
public class OnErrorResumeWithEmpty<Parsed> implements Function<Throwable, Observable<? extends Parsed>> {

    @Override
    public Observable<? extends Parsed> apply(@NonNull Throwable throwable) throws Exception {
        return Observable.empty();
    }
}
