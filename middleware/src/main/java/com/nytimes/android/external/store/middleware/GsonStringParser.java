package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import java.lang.reflect.Type;

import javax.inject.Inject;

public class GsonStringParser<Parsed> implements Parser<String, Parsed> {

    private final Gson gson;
    private final Type parsedClass;

    @Inject
    public GsonStringParser(Gson gson, Type parsedClass) {
        this.gson = gson;
        this.parsedClass = parsedClass;
    }

    @Override
    public Parsed call(String source) {
        return gson.fromJson(source, parsedClass);
    }
}
