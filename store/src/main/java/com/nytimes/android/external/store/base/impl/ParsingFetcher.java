package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;

import javax.annotation.Nonnull;

import rx.Observable;

/**
 * Parsing fetcher that takes parser of Raw type and fetcher of raw type returning parsed instance.
 * Created on 10/20/17.
 */
public class ParsingFetcher<Parsed, Raw, Key> implements Fetcher<Parsed, Key> {

    private final Fetcher<Raw, Key> rawFetcher;
    private final Parser<Raw, Parsed> parser;

    /**
     * Creates instance of ParsingFetcher
     *
     * @param rawFetcher   fetches raw data by key
     * @param parsedParser parses raw data to the instance of 'Parsed'
     */
    public ParsingFetcher(@Nonnull Fetcher<Raw, Key> rawFetcher, @Nonnull Parser<Raw, Parsed> parsedParser) {
        this.rawFetcher = rawFetcher;
        this.parser = parsedParser;
    }

    /**
     * Returns parsing fetcher for fetcher and parser
     * @param fetcher
     * @param parser
     * @param <Parsed>
     * @param <Raw>
     * @param <Key>
     * @return
     */
    public static final <Parsed, Raw, Key> ParsingFetcher<Parsed, Raw, Key> from(@Nonnull Fetcher<Raw, Key> fetcher, @Nonnull Parser<Raw, Parsed> parser) {
        return new ParsingFetcher(fetcher, parser);
    }

    @Nonnull
    @Override
    public Observable<Parsed> fetch(@Nonnull Key key) {
        return rawFetcher.fetch(key).map(parser);
    }
}
