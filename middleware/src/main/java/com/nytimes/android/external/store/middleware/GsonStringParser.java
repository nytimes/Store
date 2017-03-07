package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;
import java.lang.reflect.Type;

import javax.inject.Inject;

import static com.nytimes.android.external.cache.Preconditions.checkNotNull;

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
    public Parsed call(String source) {
        return gson.fromJson(source, type);
    }
}
