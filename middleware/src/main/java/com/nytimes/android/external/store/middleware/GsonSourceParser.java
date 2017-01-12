package com.nytimes.android.external.store.middleware;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import javax.inject.Inject;

import okio.BufferedSource;

/**
 * Parser to be used when going from a BufferedSource to any Parsed Type
 * example usage:
 * ParsingStoreBuilder.<BufferedSource, BookResults>builder()
 * .fetcher(fetcher)
 * .persister(SourcePersisterFactory.create(getApplicationContext().getCacheDir()))
 * .parser(GsonParserFactory.createSourceParser(new Gson(),BookResult.class)
 * .open();
 */


public class GsonSourceParser<Parsed> implements Parser<BufferedSource, Parsed> {

    private final Gson gson;
    private final Type type;

    @Inject
    public GsonSourceParser(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public Parsed call(@NonNull BufferedSource source) {
        try (InputStreamReader reader = new InputStreamReader(source.inputStream())) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
