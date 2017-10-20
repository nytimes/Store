package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ParsingFetcher;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParsingFetcherTest {

    static final String DATA = "Test data.";
    static final String PARSED = "DATA PARSED";

    @Mock
    Fetcher<String, BarCode> fetcher;
    @Mock
    Parser<String, String> parser;
    @Mock
    Persister<String, BarCode> persister;
    private final BarCode barCode = new BarCode("key", "value");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPersistFetcher() {

        Store<String, BarCode> simpleStore = StoreBuilder.<String>barcode()
                .fetcher(ParsingFetcher.from(fetcher, parser))
                .persister(persister)
                .open();


        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(DATA));

        when(parser.call(DATA))
                .thenReturn(PARSED);

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(PARSED));

        when(persister.write(barCode, PARSED))
                .thenReturn(Observable.just(true));

        String value = simpleStore.get(barCode).toBlocking().first();


        assertThat(value).isEqualTo(PARSED);

        verify(fetcher, times(1)).fetch(barCode);
        verify(parser, times(1)).call(DATA);

        verify(persister, times(1)).write(barCode, PARSED);
    }

}
