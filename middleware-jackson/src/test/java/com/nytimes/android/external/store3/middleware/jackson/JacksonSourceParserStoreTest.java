package com.nytimes.android.external.store3.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.jackson.data.Foo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;
import okio.Okio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JacksonSourceParserStoreTest {

    private static final String KEY = "key";
    private static final String sourceString =
            "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;
    @Mock
    Persister<BufferedSource, BarCode> persister;
    private final BarCode barCode = new BarCode("value", KEY);

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(Charset.defaultCharset()))));
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        BufferedSource bufferedSource = source(sourceString);
        assertNotNull(bufferedSource);

        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(bufferedSource));

        when(persister.read(barCode))
                .thenReturn(Maybe.<BufferedSource>empty())
                .thenReturn(Maybe.just(bufferedSource));

        when(persister.write(barCode, bufferedSource))
                .thenReturn(Single.just(true));
    }

    @Test
    public void testDefaultJacksonSourceParser() {
        Parser<BufferedSource, Foo> parser = JacksonParserFactory.createSourceParser(Foo.class);
        Store<Foo, BarCode> store = StoreBuilder.<BarCode, BufferedSource, Foo>parsedWithKey()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        Foo result = store.get(barCode).blockingGet();

        validateFoo(result);

        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testCustomJsonFactorySourceParser() {
        JsonFactory jsonFactory = new JsonFactory();

        Parser<BufferedSource, Foo> parser = JacksonParserFactory.createSourceParser(jsonFactory, Foo.class);

        Store<Foo, BarCode> store = StoreBuilder.<BarCode, BufferedSource, Foo>parsedWithKey()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        Foo result = store.get(barCode).blockingGet();

        validateFoo(result);

        verify(fetcher, times(1)).fetch(barCode);
    }

    private void validateFoo(Foo foo) {
        assertNotNull(foo);
        assertEquals(foo.number, 123);
        assertEquals(foo.string, "abc");
        assertEquals(foo.bars.size(), 2);
        assertEquals(foo.bars.get(0).string, "def");
        assertEquals(foo.bars.get(1).string, "ghi");
    }

    @Test
    public void testNullJsonFactory() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser((JsonFactory) null, Foo.class);
    }

    @Test
    public void testNullTypeWithValidJsonFactory() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser(new JsonFactory(), null);
    }

    @Test
    public void testNullObjectMapper() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser((ObjectMapper) null, Foo.class);
    }

    @Test
    public void testNullType() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser(null);
    }

}
