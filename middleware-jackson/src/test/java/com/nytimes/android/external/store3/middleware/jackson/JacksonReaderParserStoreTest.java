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

import java.io.Reader;
import java.io.StringReader;


import io.reactivex.Maybe;
import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JacksonReaderParserStoreTest {

    private static final String KEY = "key";
    private static final String sourceString =
            "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    Fetcher<Reader, BarCode> fetcher;
    @Mock
    Persister<Reader, BarCode> persister;
    private final BarCode barCode = new BarCode("value", KEY);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Reader source = new StringReader(sourceString);
        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(source));

        when(persister.read(barCode))
                .thenReturn(Maybe.<Reader>empty())
                .thenReturn(Maybe.just(source));

        when(persister.write(barCode, source))
                .thenReturn(Single.just(true));
    }

    @Test
    public void testDefaultJacksonReaderParser() {
        Parser<Reader, Foo> parser = JacksonParserFactory.createReaderParser(Foo.class);
        Store<Foo, BarCode> store = StoreBuilder.<BarCode, Reader, Foo>parsedWithKey()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        Foo result = store.get(barCode).blockingGet();

        validateFoo(result);

        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testCustomJsonFactoryReaderParser() {
        JsonFactory jsonFactory = new JsonFactory();

        Parser<Reader, Foo> parser = JacksonParserFactory.createReaderParser(jsonFactory, Foo.class);

        Store<Foo, BarCode> store = StoreBuilder.<BarCode, Reader, Foo>parsedWithKey()
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
        JacksonParserFactory.createReaderParser((JsonFactory) null, Foo.class);
    }

    @Test
    public void testNullTypeWithValidJsonFactory() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createReaderParser(new JsonFactory(), null);
    }

    @Test
    public void testNullObjectMapper() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createReaderParser((ObjectMapper) null, Foo.class);
    }

    @Test
    public void testNullType() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser(null);
    }

}
