package com.nytimes.android.external.store.util;

import rx.functions.Func2;

public interface KeyParseFunc<Key,Raw,Parsed> extends Func2<Key,Raw,Parsed> {


    @Override
     Parsed call(Key key, Raw raw);
}
