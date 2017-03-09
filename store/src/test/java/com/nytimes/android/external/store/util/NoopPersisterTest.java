package com.nytimes.android.external.store.util;

import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NoopPersisterTest {

    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void writeReadTest() {

        NoopPersister<String, BarCode> persister = NoopPersister.create();
        boolean success = persister.write(barCode, "foo").toBlocking().first();
        assertThat(success).isTrue();
        String rawValue = persister.read(barCode).toBlocking().first();
        assertThat(rawValue).isEqualTo("foo");
    }

    @Test
    public void noopParserFuncTest() {
        NoopParserFunc<String, String> noopParserFunc = new NoopParserFunc<>();
        String input = "foo";
        String output = (String) noopParserFunc.call(input);
        assertThat(input).isEqualTo(output);
        //intended object ref comparison
        assertThat(input).isSameAs(output);
    }

}
