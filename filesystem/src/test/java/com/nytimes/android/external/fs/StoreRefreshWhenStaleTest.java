package com.nytimes.android.external.fs;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.RecordState;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import okio.BufferedSource;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreRefreshWhenStaleTest {
    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;
    @Mock
    RecordPersister persister;
    @Mock
    BufferedSource network1;
    @Mock
    BufferedSource network2;
    @Mock
    BufferedSource disk1;
    @Mock
    BufferedSource disk2;

    private final BarCode barCode = new BarCode("key", "value");
    private Store<BufferedSource, BarCode> store;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .refreshOnStale()
                .open();

    }

    @Test
    public void diskWasRefreshedWhenStaleRecord() {
        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(network1));
        when(persister.read(barCode))
                .thenReturn(Observable.just(disk1));  //get should return from disk
        when(persister.getRecordState(barCode)).thenReturn(RecordState.STALE);

        when(persister.write(barCode, network1))
                .thenReturn(Observable.just(true));

        store.get(barCode).test().awaitTerminalEvent();
        verify(fetcher, times(1)).fetch(barCode);
        verify(persister, times(2)).getRecordState(barCode);
        verify(persister, times(1)).write(barCode, network1);
        verify(persister, times(2)).read(barCode); //reads from disk a second time when backfilling

    }

    @Test
    public void diskWasNotRefreshedWhenFreshRecord() {
        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(network1));
        when(persister.read(barCode))
                .thenReturn(Observable.just(disk1))  //get should return from disk
                .thenReturn(Observable.just(disk2)); //backfill should read from disk again
        when(persister.getRecordState(barCode)).thenReturn(RecordState.FRESH);

        when(persister.write(barCode, network1))
                .thenReturn(Observable.just(true));

        BufferedSource result = store.get(barCode)
                .test()
                .awaitTerminalEvent()
                .getOnNextEvents()
                .get(0);
        assertThat(result).isEqualTo(disk1);
        verify(fetcher, times(0)).fetch(barCode);
        verify(persister, times(1)).getRecordState(barCode);

        store.clear(barCode);
        result = store.get(barCode).test().awaitTerminalEvent().getOnNextEvents().get(0);
        assertThat(result).isEqualTo(disk2);
        verify(fetcher, times(0)).fetch(barCode);
        verify(persister, times(2)).getRecordState(barCode);

    }
}
