package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ParsingStoreBuilder;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StoreWithParserTest {

    private static final String DISK = "persister";
    private static final String NETWORK = "fetch";

    @Mock
    Fetcher<String> fetcher;
    @Mock
    Persister<String> persister;
    @Mock
    Parser<String, String> parser;

    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void testSimple() {
        MockitoAnnotations.initMocks(this);


        Store<String> simpleStore = ParsingStoreBuilder.<String, String>builder()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(DISK));

        when(persister.write(barCode, NETWORK))
                .thenReturn(Observable.just(true));

        when(parser.call(DISK)).thenReturn(barCode.getKey());

        String value = simpleStore.get(barCode).toBlocking().first();
        assertThat(value).isEqualTo(barCode.getKey());
        value = simpleStore.get(barCode).toBlocking().first();
        assertThat(value).isEqualTo(barCode.getKey());
        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testSubclass() {
        MockitoAnnotations.initMocks(this);

        Store<String> simpleStore = new SampleParsingStore(fetcher, persister, parser);

        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(DISK));

        when(persister.write(barCode, NETWORK))
                .thenReturn(Observable.just(true));

        when(parser.call(DISK)).thenReturn(barCode.getKey());

        String value = simpleStore.get(barCode).toBlocking().first();
        assertThat(value).isEqualTo(barCode.getKey());
        value = simpleStore.get(barCode).toBlocking().first();
        assertThat(value).isEqualTo(barCode.getKey());
        verify(fetcher, times(1)).fetch(barCode);
    }
}
