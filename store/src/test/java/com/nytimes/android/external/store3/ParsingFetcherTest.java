package com.nytimes.android.external.store3;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.ParsingFetcher;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Maybe;
import io.reactivex.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParsingFetcherTest {

    static final String RAW_DATA = "Test data.";
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
                .thenReturn(Single.just(RAW_DATA));

        when(parser.apply(RAW_DATA))
                .thenReturn(PARSED);

        when(persister.read(barCode))
                .thenReturn(Maybe.just(PARSED));

        when(persister.write(barCode, PARSED))
                .thenReturn(Single.just(true));

        String value = simpleStore.fetch(barCode).blockingGet();

        assertThat(value).isEqualTo(PARSED);

        verify(fetcher, times(1)).fetch(barCode);
        verify(parser, times(1)).apply(RAW_DATA);

        verify(persister, times(1)).write(barCode, PARSED);
    }
}
