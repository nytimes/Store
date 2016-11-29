package com.nytimes.android.external.store.util;

import rx.functions.Func1;

/**
 * Pass-through parser for stores that parse externally
 */
public class NoopParserFunc<Raw, Parsed> implements Func1<Raw, Parsed> {
    @Override
    public Object call(Object object) {
        return object;
    }
}
