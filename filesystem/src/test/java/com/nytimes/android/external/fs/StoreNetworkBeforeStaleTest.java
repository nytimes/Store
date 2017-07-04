package com.nytimes.android.external.fs;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.RecordState;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import okio.BufferedSource;
import rx.Observable;
import rx.observers.AssertableSubscriber;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreNetworkBeforeStaleTest {
    final BarCode barCode = new BarCode("key", "value");
    final Exception sorry = new Exception("sorry");
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
    private Store<BufferedSource, BarCode> store;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .networkBeforeStale()
                .open();
    }

    @Test
    public void networkBeforeDiskWhenStale() {
        when(fetcher.fetch(barCode))
                .thenReturn(Observable.<BufferedSource>error(new Exception()));
        when(persister.read(barCode))
                .thenReturn(Observable.just(disk1));  //get should return from disk
        when(persister.getRecordState(barCode)).thenReturn(RecordState.STALE);

        when(persister.write(barCode, network1))
                .thenReturn(Observable.just(true));

        store.get(barCode).test().awaitTerminalEvent();

        InOrder inOrder = inOrder(fetcher, persister);
        inOrder.verify(fetcher, times(1)).fetch(barCode);
        inOrder.verify(persister, times(1)).read(barCode);
        verify(persister, never()).write(barCode, network1);
    }

    @Test
    public void noNetworkBeforeStaleWhenMissingRecord() {
        when(fetcher.fetch(barCode))
                .thenReturn(Observable.just(network1));
        when(persister.read(barCode))
                .thenReturn(Observable.<BufferedSource>empty(), Observable.just(disk1));  //first call should return
        // empty, second call after network should return the network value
        when(persister.getRecordState(barCode)).thenReturn(RecordState.MISSING);

        when(persister.write(barCode, network1))
                .thenReturn(Observable.just(true));

        store.get(barCode).test().awaitTerminalEvent();

        InOrder inOrder = inOrder(fetcher, persister);
        inOrder.verify(persister, times(1)).read(barCode);
        inOrder.verify(fetcher, times(1)).fetch(barCode);
        inOrder.verify(persister, times(1)).write(barCode, network1);
        inOrder.verify(persister, times(1)).read(barCode);
    }

    @Test
    public void noNetworkBeforeStaleWhenFreshRecord() {
        when(persister.read(barCode))
                .thenReturn(Observable.just(disk1));  //get should return from disk
        when(persister.getRecordState(barCode)).thenReturn(RecordState.FRESH);

        store.get(barCode).test().awaitTerminalEvent();

        verify(fetcher, never()).fetch(barCode);
        verify(persister, never()).write(barCode, network1);
        verify(persister, times(1)).read(barCode);
    }

    @Test
    public void networkBeforeStaleNoNetworkResponse() {
        Observable<BufferedSource> exception = Observable.error(sorry);
        when(fetcher.fetch(barCode))
                .thenReturn(exception);
        when(persister.read(barCode))
                .thenReturn(exception, exception);  //first call should return
        // empty, second call after network should return the network value
        when(persister.getRecordState(barCode)).thenReturn(RecordState.MISSING);

        when(persister.write(barCode, network1))
                .thenReturn(Observable.just(true));

        AssertableSubscriber<BufferedSource> subscriber = store.get(barCode).test().awaitTerminalEvent();
        subscriber.assertError(sorry);

        InOrder inOrder = inOrder(fetcher, persister);
        inOrder.verify(persister, times(1)).read(barCode);
        inOrder.verify(fetcher, times(1)).fetch(barCode);
        inOrder.verify(persister, times(1)).read(barCode);
    }
}
