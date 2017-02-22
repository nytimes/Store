package com.nytimes.android.external.store.util;

import com.nytimes.android.external.store.base.Parser;

import javax.annotation.Nonnull;

public class NoKeyParseFunc<Key, Raw, Parsed> implements KeyParseFunc<Key, Raw, Parsed> {
    private final Parser<Raw, Parsed> parser;

    public NoKeyParseFunc(@Nonnull Parser<Raw, Parsed> parser) {
        this.parser = parser;
    }

    @Override
    @Nonnull
    public Parsed call(@Nonnull Key key, @Nonnull Raw raw) {
        return parser.call(raw);
    }
}
