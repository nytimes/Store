package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.util.ParserException;

import java.util.ArrayList;
import java.util.List;

import static com.nytimes.android.external.cache.Preconditions.checkArgument;
import static com.nytimes.android.external.cache.Preconditions.checkNotNull;

public class MultiParser<Raw, Parsed> implements Parser<Raw, Parsed> {

    private final List<Parser> parsers = new ArrayList<>();

    public MultiParser(List<Parser> parsers) {
        checkNotNull(parsers, "Parsers can't be null.");
        checkArgument(!parsers.isEmpty(), "Parsers can't be empty.");
        for (Parser parser : parsers) {
            checkNotNull(parser, "Parser can't be null.");
        }
        this.parsers.addAll(parsers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Parsed call(Raw raw) {
        Object parsed = raw;
        for (Parser parser : parsers) {
            try {
                parsed = parser.call(parsed);
            } catch (ClassCastException exception) {
                throw createParserException();
            }
        }
        return (Parsed) parsed;
    }

    private ParserException createParserException() {
        return new ParserException("One of the provided parsers has a wrong typing. " +
                "Make sure that parsers are passed in a correct order and the types match each other.");
    }
}
