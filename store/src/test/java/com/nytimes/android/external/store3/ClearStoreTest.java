package com.nytimes.android.external.store3;

import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static com.nytimes.android.external.store3.GetRefreshingTest.ClearingPersister;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClearStoreTest {
    @Mock
    ClearingPersister persister;
    AtomicInteger networkCalls;
    private Store<Integer, BarCode> store;

    @Before
    public void setUp() {
        networkCalls = new AtomicInteger(0);
        store = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> Single.fromCallable(() -> networkCalls.incrementAndGet()))
                .persister(persister)
                .open();
    }

    @Test
    public void testClearSingleBarCode() {
        // one request should produce one call
        BarCode barcode = new BarCode("type", "key");

        when(persister.read(barcode))
                .thenReturn(Maybe.<Integer>empty()) //read from disk on get
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.<Integer>empty()) //read from disk after clearing
                .thenReturn(Maybe.just(1)); //read from disk after making additional network call
        when(persister.write(barcode, 1)).thenReturn(Single.just(true));
        when(persister.write(barcode, 2)).thenReturn(Single.just(true));
        when(persister.clear(barcode)).thenReturn(Completable.complete());

        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(1);

        // after clearing the memory another call should be made
        store.clear(barcode).test().awaitTerminalEvent();
        store.get(barcode).test().awaitTerminalEvent();
        verify(persister).clear(barcode);
        assertThat(networkCalls.intValue()).isEqualTo(2);
    }

    @Test
    public void testClearAllBarCodes() {
        BarCode barcode1 = new BarCode("type1", "key1");
        BarCode barcode2 = new BarCode("type2", "key2");

        when(persister.read(barcode1))
                .thenReturn(Maybe.<Integer>empty()) //read from disk
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Maybe.just(1)); //read from disk after making additional network call
        when(persister.write(barcode1, 1)).thenReturn(Single.just(true));
        when(persister.write(barcode1, 2)).thenReturn(Single.just(true));

        when(persister.read(barcode2))
                .thenReturn(Maybe.<Integer>empty()) //read from disk
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Maybe.just(1)); //read from disk after making additional network call

        when(persister.write(barcode2, 1)).thenReturn(Single.just(true));
        when(persister.write(barcode2, 2)).thenReturn(Single.just(true));

        when(persister.clear(barcode1)).thenReturn(Completable.complete());
        when(persister.clear(barcode2)).thenReturn(Completable.complete());

        // each request should produce one call
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(2);

        store.clear().test().awaitTerminalEvent();

        // after everything is cleared each request should produce another 2 calls
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(4);
    }
}
