package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import java.io.Reader;

import javax.inject.Inject;

public class GsonReaderParser<Parsed> implements Parser<Reader, Parsed> {

    private final Gson gson;
    private final Class<Parsed> parsedClass;

    @Inject
    public GsonReaderParser(Gson gson, Class<Parsed> parsedClass) {
        this.gson = gson;
        this.parsedClass = parsedClass;
    }

    @Override
    public Parsed call(Reader reader) {
        return gson.fromJson(reader, parsedClass);
    }
}
