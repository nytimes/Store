package com.nytimes.android.external.store.util;

import com.nytimes.android.external.store.base.Parser;

import javax.annotation.Nonnull;

import io.reactivex.annotations.NonNull;

public class NoKeyParser<Key, Raw, Parsed> implements KeyParser<Key, Raw, Parsed> {
    private final Parser<Raw, Parsed> parser;

    public NoKeyParser(@Nonnull Parser<Raw, Parsed> parser) {
        this.parser = parser;
    }

    @Override
    public Parsed apply(@NonNull Key key, @NonNull Raw raw) throws Exception {
        return parser.apply(raw);
    }
}
