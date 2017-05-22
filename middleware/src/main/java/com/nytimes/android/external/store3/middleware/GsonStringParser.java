package com.nytimes.android.external.store3.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.util.ParserException;

import java.lang.reflect.Type;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

import static com.nytimes.android.external.cache3.Preconditions.checkNotNull;

public class GsonStringParser<Parsed> implements Parser<String, Parsed> {

    private final Gson gson;
    private final Type type;

    @Inject
    public GsonStringParser(Gson gson, Type parsedClass) {
        checkNotNull(gson, "Gson can't be null");
        checkNotNull(parsedClass, "Type can't be null");
        this.gson = gson;
        this.type = parsedClass;
    }

    @Override
    public Parsed apply(@NonNull String s) throws ParserException {
        return gson.fromJson(s, type);
    }
}
