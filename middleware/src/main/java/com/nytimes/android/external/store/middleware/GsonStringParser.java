package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import javax.inject.Inject;

public class GsonStringParser<Parsed> implements Parser<String, Parsed> {

    private final Gson gson;
    private final Class<Parsed> parsedClass;

    @Inject
    public GsonStringParser(Gson gson, Class<Parsed> parsedClass) {
        this.gson = gson;
        this.parsedClass = parsedClass;
    }

    @Override
    public Parsed call(String source) {
        return gson.fromJson(source, parsedClass);
    }
}
