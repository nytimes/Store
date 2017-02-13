package com.nytimes.android.external.store.util;

import com.nytimes.android.external.store.base.Parser;

/**
 * Pass-through parser for stores that parse externally
 */
public class NoopParserFunc<Raw, Parsed> implements Parser<Raw, Parsed> {
    @Override
    public Object call(Object object) {
        return object;
    }
}
