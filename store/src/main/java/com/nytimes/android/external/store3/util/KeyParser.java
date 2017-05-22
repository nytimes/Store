package com.nytimes.android.external.store3.util;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;

public interface KeyParser<Key, Raw, Parsed> extends BiFunction<Key, Raw, Parsed> {

    @Override
    Parsed apply(@NonNull Key key, @NonNull Raw raw) throws ParserException;

}
