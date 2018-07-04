package com.nytimes.android.external.store3.base.impl;

import com.nytimes.android.external.store3.util.KeyParser;
import com.nytimes.android.external.store3.util.ParserException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

import static com.nytimes.android.external.store3.storecache.Preconditions.checkArgument;
import static com.nytimes.android.external.store3.storecache.Preconditions.checkNotNull;


public class MultiParser<Key, Raw, Parsed> implements KeyParser<Key, Raw, Parsed> {

    private final List<KeyParser> parsers = new ArrayList<>();

    public MultiParser(List<KeyParser> parsers) {
        checkNotNull(parsers, "Parsers can't be null.");
        checkArgument(!parsers.isEmpty(), "Parsers can't be empty.");
        for (KeyParser parser : parsers) {
            checkNotNull(parser, "Parser can't be null.");
        }
        this.parsers.addAll(parsers);
    }

    private ParserException createParserException() {
        return new ParserException("One of the provided parsers has a wrong typing. " +
                "Make sure that parsers are passed in a correct order and the fromTypes match each other.");
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public Parsed apply(@NonNull Key key, @NonNull Raw raw) throws ParserException {
        Object parsed = raw;
        for (KeyParser parser : parsers) {
            try {
                parsed = parser.apply(key, parsed);
            } catch (ClassCastException exception) {
                throw createParserException();
            }
        }
        return (Parsed) parsed;
    }
}
