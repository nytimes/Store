package com.nytimes.android.external.fs3;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.RecordState;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreNetworkBeforeStaleTest {

    Exception sorry = new Exception("sorry");
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
                .networkBeforeStale()
                .open();

    }

    @Test
    public void networkBeforeDiskWhenStale() {
        when(fetcher.fetch(barCode))
                .thenReturn(Single.<BufferedSource>error(new Exception()));
        when(persister.read(barCode))
                .thenReturn(Maybe.just(disk1));  //get should return from disk
        when(persister.getRecordState(barCode)).thenReturn(RecordState.STALE);

        when(persister.write(barCode, network1))
                .thenReturn(Single.just(true));

        store.get(barCode).test().awaitTerminalEvent();

        InOrder inOrder = inOrder(fetcher, persister);
        inOrder.verify(fetcher, times(1)).fetch(barCode);
        inOrder.verify(persister, times(1)).read(barCode);
        verify(persister, never()).write(barCode, network1);
    }

    @Test
    public void noNetworkBeforeStaleWhenMissingRecord() {
        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(network1));
        when(persister.read(barCode))
                .thenReturn(Maybe.<BufferedSource>empty(), Maybe.just(disk1));  //first call should return
        // empty, second call after network should return the network value
        when(persister.getRecordState(barCode)).thenReturn(RecordState.MISSING);

        when(persister.write(barCode, network1))
                .thenReturn(Single.just(true));

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
                .thenReturn(Maybe.just(disk1));  //get should return from disk
        when(persister.getRecordState(barCode)).thenReturn(RecordState.FRESH);

        store.get(barCode).test().awaitTerminalEvent();

        verify(fetcher, never()).fetch(barCode);
        verify(persister, never()).write(barCode, network1);
        verify(persister, times(1)).read(barCode);
    }

    @Test
    public void networkBeforeStaleNoNetworkResponse() {
        Single<BufferedSource> singleError = Single.error(sorry);
        Maybe<BufferedSource> maybeError = Maybe.error(sorry);
        when(fetcher.fetch(barCode))
                .thenReturn(singleError);
        when(persister.read(barCode))
                .thenReturn(maybeError, maybeError);  //first call should return
        // empty, second call after network should return the network value
        when(persister.getRecordState(barCode)).thenReturn(RecordState.MISSING);

        when(persister.write(barCode, network1))
                .thenReturn(Single.just(true));

        store.get(barCode).test().assertError(sorry);

        InOrder inOrder = inOrder(fetcher, persister);
        inOrder.verify(persister, times(1)).read(barCode);
        inOrder.verify(fetcher, times(1)).fetch(barCode);
        inOrder.verify(persister, times(1)).read(barCode);
    }
}
