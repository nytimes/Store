package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import okio.BufferedSource;

/**
 * Parser to be used when going from a BufferedSource to any Parsed Type
 * example usage:
 * ParsingStoreBuilder.<BufferedSource, BookResults>builder()
 * .fetcher(fetcher)
 * .persister(new SourcePersister(fileSystem))
 * .parser(new GsonSourceParser<>(gson, BookResults.class))
 * .open();
 */


public class GsonSourceParser2<Parsed> implements Parser<BufferedSource, Parsed> {
    private final Gson gson;
    private Type type;

    @Inject
    public GsonSourceParser2(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
        Class<String> stringClass = String.class;
    }

    @Override
    public Parsed call(BufferedSource source) {
        try (InputStreamReader reader = new InputStreamReader(source.inputStream())) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
