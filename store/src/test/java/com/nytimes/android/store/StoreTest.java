package com.nytimes.android.store;

import com.nytimes.android.store.base.Fetcher;
import com.nytimes.android.store.base.Persister;
import com.nytimes.android.store.base.Store;
import com.nytimes.android.store.base.impl.BarCode;
import com.nytimes.android.store.base.impl.StoreBuilder;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StoreTest {

    private static final String DISK = "disk";
    private static final String NETWORK = "fetch";

    @Mock
    Fetcher<String> fetcher;
    @Mock
    Persister<String> persister;
    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void testSimple() {
        MockitoAnnotations.initMocks(this);


        Store<String> simpleStore = new StoreBuilder<String>()
                .persister(persister)
                .fetcher(fetcher)
                .open();


        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(DISK));

        when(persister.write(barCode, NETWORK))
                .thenReturn(Observable.just(true));

        String value = simpleStore.get(barCode).toBlocking().first();

        assertThat(value).isEqualTo(DISK);
        value = simpleStore.get(barCode).toBlocking().first();
        assertThat(value).isEqualTo(DISK);
        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testSubclass() {
        MockitoAnnotations.initMocks(this);


        Store<String> simpleStore = new SampleStore(fetcher, persister);
        simpleStore.clearMemory();

        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(DISK));
        when(persister.write(barCode, NETWORK)).thenReturn(Observable.just(true));

        String value = simpleStore.get(barCode).toBlocking().first();
        assertThat(value).isEqualTo(DISK);
        value = simpleStore.get(barCode).toBlocking().first();
        assertThat(value).isEqualTo(DISK);
        verify(fetcher, times(1)).fetch(barCode);
    }
}
