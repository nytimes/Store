package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.util.KeyParser;
import com.nytimes.android.external.store.util.NoKeyParser;
import com.nytimes.android.external.store.util.ParserException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class MultiParserTest {

    private static final Parser<Integer, String> PARSER_1 = new Parser<Integer, String>() {
        @Override
        public String call(Integer value) {
            return String.valueOf(value);
        }
    };

    private static final Parser<String, BarCode> PARSER_2 = new Parser<String, BarCode>() {
        @Override
        public BarCode call(String value) {
            return new BarCode(value, "KEY");
        }
    };

    private static final Parser<BarCode, UUID> PARSER_3 = new Parser<BarCode, UUID>() {
        @Override
        public UUID call(BarCode barCode) {
            return UUID.randomUUID();
        }
    };

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldParseChainProperly() {
        List<KeyParser> parsersChain = new ArrayList<>();
        parsersChain.add(new NoKeyParser<>(PARSER_1));
        parsersChain.add(new NoKeyParser<>(PARSER_2));
        parsersChain.add(new NoKeyParser<>(PARSER_3));

        KeyParser<Object, Integer, UUID> parser = new MultiParser<>(parsersChain);
        UUID parsed = parser.call(new Object(), 100);

        assertNotNull(parsed);
    }

    @Test
    public void shouldFailIfOneOfParsersIsInvalid() {
        expectedException.expect(ParserException.class);

        List<KeyParser> parsersChain = new ArrayList<>();
        parsersChain.add(new NoKeyParser<>(PARSER_1));
        parsersChain.add(new NoKeyParser<>(PARSER_3));
        parsersChain.add(new NoKeyParser<>(PARSER_2));

        KeyParser<Object, Integer, UUID> parser = new MultiParser<>(parsersChain);
        UUID parsed = parser.call(new Object(), 100);

        assertNotNull(parsed);
    }

}
