package com.nytimes.android.external.store3.util;

import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.MemoryPolicy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.concurrent.TimeUnit;

public class NoopPersisterTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    @Test
    public void writeReadTest() {
        BarCode barCode = new BarCode("key", "value");
        NoopPersister<String, BarCode> persister = NoopPersister.create();
        boolean success = persister.write(barCode, "foo").blockingGet();
        assertThat(success).isTrue();
        String rawValue = persister.read(barCode).blockingGet();
        assertThat(rawValue).isEqualTo("foo");
    }

    @Test
    public void noopParserFuncTest() {
        NoopParserFunc<String, String> noopParserFunc = new NoopParserFunc<>();
        String input = "foo";
        String output = noopParserFunc.apply(input);
        assertThat(input).isEqualTo(output);
        //intended object ref comparison
        assertThat(input).isSameAs(output);
    }

    // https://github.com/NYTimes/Store/issues/312
    @Test
    public void testReadingOfMemoryPolicies() {
        MemoryPolicy expireAfterWritePolicy = MemoryPolicy.builder()
            .setExpireAfterWrite(1)
            .setExpireAfterTimeUnit(TimeUnit.HOURS)
            .build();
        NoopPersister.create(expireAfterWritePolicy);

        MemoryPolicy expireAfterAccessPolicy = MemoryPolicy.builder()
            .setExpireAfterAccess(1)
            .setExpireAfterTimeUnit(TimeUnit.HOURS)
            .build();
        NoopPersister.create(expireAfterAccessPolicy);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No expiry policy set");
        MemoryPolicy incompletePolicy = MemoryPolicy.builder()
            .setExpireAfterTimeUnit(TimeUnit.HOURS)
            .build();
        NoopPersister.create(incompletePolicy);
    }
}
