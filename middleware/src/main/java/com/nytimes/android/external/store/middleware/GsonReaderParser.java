package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.lang.reflect.Type;

import javax.inject.Inject;

import static com.nytimes.android.external.cache.Preconditions.checkNotNull;

public class GsonReaderParser<Parsed> implements Parser<Reader, Parsed> {

    private final Gson gson;
    private final Type type;

    @Inject
    public GsonReaderParser(Gson gson, Type type) {
        checkNotNull(gson, "Gson can't be null");
        checkNotNull(type, "Type can't be null");
        this.gson = gson;
        this.type = type;
    }

    @Override
    public Parsed call(@NotNull Reader reader) {
        return gson.fromJson(reader, type);
    }
}
