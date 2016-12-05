package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.RealStore;
import com.nytimes.android.external.store.base.impl.StoreBuilder;
import com.nytimes.android.external.store.util.NoopPersister;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSimple() {

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


    @Test
    public void testNoopAndDefault() {

        NoopPersister<String> persister = spy(new NoopPersister<String>());
        Store<String> simpleStore = new RealStore<>(fetcher, persister);
        simpleStore.clearMemory();

        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(NETWORK));

        String value = simpleStore.get(barCode).toBlocking().first();
        verify(fetcher, times(1)).fetch(barCode);
        verify(persister, times(1)).write(barCode, NETWORK);
        verify(persister, times(2)).read(barCode);
        assertThat(value).isEqualTo(NETWORK);


        value = simpleStore.get(barCode).toBlocking().first();
        verify(persister, times(2)).read(barCode);
        verify(persister, times(1)).write(barCode, NETWORK);
        verify(fetcher, times(1)).fetch(barCode);

        assertThat(value).isEqualTo(NETWORK);
    }

}
