package com.nytimes.android.external.store.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ParsingStoreBuilder;
import com.nytimes.android.external.store.middleware.jackson.data.Foo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import okio.BufferedSource;
import okio.Okio;
import rx.Observable;

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
    Fetcher<BufferedSource> fetcher;
    @Mock
    Persister<BufferedSource> persister;

    private final BarCode barCode = new BarCode("value", KEY);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        BufferedSource bufferedSource = source(sourceString);
        assertNotNull(bufferedSource);

        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(bufferedSource));

        when(persister.read(barCode))
                .thenReturn(Observable.<BufferedSource>empty())
                .thenReturn(Observable.just(bufferedSource));

        when(persister.write(barCode, bufferedSource))
                .thenReturn(Observable.just(true));
    }

    @Test
    public void testDefaultJacksonStringParser() {
        Parser<BufferedSource, Foo> parser = JacksonParserFactory.createSourceParser(Foo.class);
        Store<Foo> store = ParsingStoreBuilder.<BufferedSource, Foo>builder()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        Foo result = store.get(barCode).toBlocking().first();

        validateFoo(result);

        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testCustomJsonFactoryStringParser() {
        JsonFactory jsonFactory = new JsonFactory();

        Parser<BufferedSource, Foo> parser = JacksonParserFactory.createSourceParser(jsonFactory, Foo.class);

        Store<Foo> store = ParsingStoreBuilder.<BufferedSource, Foo>builder()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        Foo result = store.get(barCode).toBlocking().first();

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

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(Charset.defaultCharset()))));
    }

}
