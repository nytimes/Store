package com.nytimes.android.external.store3.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.util.ParserException;

import java.io.Reader;
import java.lang.reflect.Type;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

import static com.nytimes.android.external.cache3.Preconditions.checkNotNull;

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
    public Parsed apply(@NonNull Reader reader) throws ParserException {
        return gson.fromJson(reader, type);
    }
}
