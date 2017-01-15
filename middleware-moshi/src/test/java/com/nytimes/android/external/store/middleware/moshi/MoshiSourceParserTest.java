package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.IBarCode;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ParsingStoreBuilder;
import com.nytimes.android.external.store.middleware.moshi.data.Foo;

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

public class MoshiSourceParserTest {

    private static final String KEY = "key";
    private static final String sourceString =
            "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Fetcher<BufferedSource> fetcher;
    @Mock
    Persister<BufferedSource> persister;

    private final IBarCode IBarCode = new BarCode("value", KEY);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        BufferedSource bufferedSource = source(sourceString);
        assertNotNull(bufferedSource);

        when(fetcher.fetch(IBarCode))
                .thenReturn(Observable.just(bufferedSource));

        when(persister.read(IBarCode))
                .thenReturn(Observable.<BufferedSource>empty())
                .thenReturn(Observable.just(bufferedSource));

        when(persister.write(IBarCode, bufferedSource))
                .thenReturn(Observable.just(true));
    }

    @Test
    public void testSourceParser() throws Exception {

        Parser<BufferedSource, Foo> parser = MoshiParserFactory.createSourceParser(Foo.class);

        Store<Foo> store = ParsingStoreBuilder.<BufferedSource, Foo>builder()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        Foo result = store.get(IBarCode).toBlocking().first();

        assertEquals(result.number, 123);
        assertEquals(result.string, "abc");
        assertEquals(result.bars.size(), 2);
        assertEquals(result.bars.get(0).string, "def");
        assertEquals(result.bars.get(1).string, "ghi");

        verify(fetcher, times(1)).fetch(IBarCode);

    }

    @Test
    public void testNullMoshi() {
        expectedException.expect(NullPointerException.class);
        MoshiParserFactory.createSourceParser(null, Foo.class);
    }

    @Test
    public void testNullType() {
        expectedException.expect(NullPointerException.class);
        MoshiParserFactory.createSourceParser(null, Foo.class);
    }

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(Charset.defaultCharset()))));
    }

}
