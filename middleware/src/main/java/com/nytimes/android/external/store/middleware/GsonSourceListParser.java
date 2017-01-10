package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.store.base.Parser;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.inject.Inject;
import okio.BufferedSource;

/**
 * Parser to be used when going from a BufferedSource to any Parsed List<Type>
 * example usage:
 * ParsingStoreBuilder.<BufferedSource, List<BookResults>>builder()
 * .fetcher(fetcher)
 * .persister(new SourcePersister(fileSystem))
 * .parser(new GsonSourceListParser<>(gson, new TypeToken<List<BookResults>())
 * .open();
 */


public class GsonSourceListParser<Parsed> implements Parser<BufferedSource, Parsed> {

    private final Gson gson;
    private final TypeToken<Parsed> parsedTypeToken;

    @Inject
    public GsonSourceListParser(Gson gson, TypeToken<Parsed> parsedTypeToken) {
        this.gson = gson;
        this.parsedTypeToken = parsedTypeToken;
    }

    @Override
    public Parsed call(BufferedSource source) {
        try (InputStreamReader reader = new InputStreamReader(source.inputStream())) {
            return gson.fromJson(reader, parsedTypeToken.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
