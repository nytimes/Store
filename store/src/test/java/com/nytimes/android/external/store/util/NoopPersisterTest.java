package com.nytimes.android.external.store.util;

import com.nytimes.android.external.store.base.IBarCode;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NoopPersisterTest {

    private final IBarCode IBarCode = new BarCode("key", "value");

    @Test
    public void writeReadTest() {

        NoopPersister<String> persister = new NoopPersister<>();
        boolean success = persister.write(IBarCode, "foo").toBlocking().first();
        assertThat(success).isTrue();
        String rawValue = persister.read(IBarCode).toBlocking().first();
        assertThat(rawValue).isEqualTo("foo");
    }

    @Test
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void noopParserFuncTest() {
        NoopParserFunc<String, String> noopParserFunc = new NoopParserFunc<>();
        String input = "foo";
        String output = (String) noopParserFunc.call(input);
        assertThat(input).isEqualTo(output);
        //intended object ref comparison
        assertThat(input == output).isTrue();
    }

}
