package com.nytimes.android.external.store3;

import com.nytimes.android.external.store3.base.Clearable;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Single;

public class ClearingPersister implements Persister<Integer, BarCode>, Clearable<BarCode> {
    @Override
    public void clear(@Nonnull BarCode key) {
        throw new RuntimeException();
    }

    @Nonnull
    @Override
    public Maybe<Integer> read(@Nonnull BarCode barCode) {
        throw new RuntimeException();
    }

    @Nonnull
    @Override
    public Single<Boolean> write(@Nonnull BarCode barCode, @Nonnull Integer integer) {
        throw new RuntimeException();
    }
}
