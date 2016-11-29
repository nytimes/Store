package com.nytimes.android.external.store.base;

import rx.functions.Func1;

//just a marker interface allowing for a reimplementation of how the parser is implemented
public interface Parser<Raw, Parsed> extends Func1<Raw, Parsed> {
}
