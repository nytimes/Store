package com.nytimes.android.external.store.util;

import rx.Observable;
import rx.functions.Func1;

/**
 * Resume with empty observable on error
 */
public class OnErrorResumeWithEmpty<Parsed> implements Func1<Throwable, Observable<? extends Parsed>> {

    @Override
    public Observable<? extends Parsed> call(Throwable throwable) {
        return Observable.empty();
    }
}
