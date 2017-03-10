package com.nytimes.android.external.store.util;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;

public interface KeyParser<Key, Raw, Parsed> extends BiFunction<Key, Raw, Parsed> {

    @Override
    @NonNull
    Parsed apply(@NonNull Key key, @NonNull Raw raw) throws ParserException;

}
