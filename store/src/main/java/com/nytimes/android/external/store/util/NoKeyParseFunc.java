package com.nytimes.android.external.store.util;

import com.nytimes.android.external.store.base.Parser;

public class NoKeyParseFunc<Key,Raw,Parsed> implements KeyParseFunc<Key, Raw,Parsed> {
    private Parser<Raw, Parsed> parser;

    public NoKeyParseFunc(Parser<Raw,Parsed> parser) {
        this.parser = parser;
    }

    @Override
    public Parsed call(Key key, Raw raw) {
        return parser.call(raw);
    }
}
