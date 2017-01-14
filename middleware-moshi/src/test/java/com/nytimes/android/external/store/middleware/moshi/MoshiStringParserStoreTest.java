package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ParsingStoreBuilder;
import com.squareup.moshi.Moshi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoshiStringParserStoreTest {

    private static final String KEY = "key";
    private static final String source =
            "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";

    @Mock
    Fetcher<String> fetcher;
    @Mock
    Persister<String> persister;

    private final BarCode barCode = new BarCode("value", KEY);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(source));

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(source));

        when(persister.write(barCode, source))
                .thenReturn(Observable.just(true));
    }

    @Test
    public void testMoshiString() {
        Store<Foo> store = ParsingStoreBuilder.<String, Foo>builder()
                .persister(persister)
                .fetcher(fetcher)
                .parser(MoshiParserFactory.createStringParser(Foo.class))
                .open();

        Foo result = store.get(barCode).toBlocking().first();

        assertEquals(result.number, 123);
        assertEquals(result.string, "abc");
        assertEquals(result.bars.size(), 2);
        assertEquals(result.bars.get(0).string, "def");
        assertEquals(result.bars.get(1).string, "ghi");

        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testInvalidArgumentsInFactory() {
        try {
            MoshiParserFactory.createStringParser(null, Foo.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("moshi cannot be null", expected.getMessage());
        }

        try {
            MoshiParserFactory.createStringParser(new Moshi.Builder().build(), null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("type cannot be null", expected.getMessage());
        }
    }


    private static class Foo {
        int number;
        String string;
        List<Bar> bars;

        public Foo(int number, String string, List<Bar> bars) {
            this.number = number;
            this.string = string;
            this.bars = bars;
        }
    }

    private static class Bar {
        String string;

        public Bar(String string) {
            this.string = string;
        }
    }
}
