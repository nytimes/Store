package com.nytimes.android.external.store.util;

import javax.annotation.Nonnull;

import rx.functions.Func2;

public interface KeyParser<Key, Raw, Parsed> extends Func2<Key, Raw, Parsed> {


    @Override
    @Nonnull
    Parsed call(@Nonnull Key key, @Nonnull Raw raw);
}
