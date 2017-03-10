package com.nytimes.android.external.store.base;

import io.reactivex.functions.Function;

//just a marker interface allowing for a reimplementation of how the parser is implemented
public interface Parser<Raw, Parsed> extends Function<Raw, Parsed> {
}
