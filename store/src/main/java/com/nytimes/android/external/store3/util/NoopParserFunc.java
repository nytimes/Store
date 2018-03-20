package com.nytimes.android.external.store3.util;

import com.nytimes.android.external.store3.base.Parser;

import io.reactivex.annotations.NonNull;

/**
 * Pass-through parser for stores that parse externally
 */
public class NoopParserFunc<Raw, Parsed> implements Parser<Raw, Parsed> {

    @Override
    public Parsed apply(@NonNull Raw raw) throws ParserException {
        //noinspection unchecked
        return (Parsed) raw;
    }
}
