package com.nytimes.android.external.store.util;

import javax.annotation.Nonnull;

import rx.functions.Func2;

public interface KeyParseFunc<Key, Raw, Parsed> extends Func2<Key, Raw, Parsed> {


    @Override
    @Nonnull
    Parsed call(@Nonnull Key key, @Nonnull Raw raw);
}
