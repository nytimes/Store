package com.nytimes.android.external.store3.middleware.moshi;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.moshi.data.Foo;
import com.squareup.moshi.Moshi;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Maybe;
import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoshiStringParserStoreTest {

    private static final String KEY = "key";
    private static final String source =
            "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    Fetcher<String, BarCode> fetcher;
    @Mock
    Persister<String, BarCode> persister;
    private final BarCode barCode = new BarCode("value", KEY);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(source));

        when(persister.read(barCode))
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(source));

        when(persister.write(barCode, source))
                .thenReturn(Single.just(true));
    }

    @Test
    public void testMoshiString() {
        Store<Foo, BarCode> store = ParsingStoreBuilder.<String, Foo>builder()
                .persister(persister)
                .fetcher(fetcher)
                .parser(MoshiParserFactory.createStringParser(Foo.class))
                .open();

        Foo result = store.get(barCode).blockingGet();

        assertEquals(result.number, 123);
        assertEquals(result.string, "abc");
        assertEquals(result.bars.size(), 2);
        assertEquals(result.bars.get(0).string, "def");
        assertEquals(result.bars.get(1).string, "ghi");

        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testNullMoshi() {
        expectedException.expect(NullPointerException.class);
        MoshiParserFactory.createStringParser(null, Foo.class);
    }

    @Test
    public void testNullType() {
        expectedException.expect(NullPointerException.class);
        MoshiParserFactory.createStringParser(new Moshi.Builder().build(), null);
    }

}
