package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import rx.Observable;

import static com.nytimes.android.external.store.GetRefreshingTest.ClearingPersister;
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
                .fetcher(new Fetcher<Integer, BarCode>() {
                    @Nonnull
                    @Override
                    public Observable<Integer> fetch(@Nonnull BarCode barCode) {
                        return Observable.fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() {
                                return networkCalls.incrementAndGet();
                            }
                        });
                    }
                })
                .persister(persister)
                .open();
    }

    @Test
    public void testClearSingleBarCode() {
        // one request should produce one call
        BarCode barcode = new BarCode("type", "key");

        when(persister.read(barcode))
                .thenReturn(Observable.<Integer>empty()) //read from disk on get
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing
                .thenReturn(Observable.just(1)); //read from disk after making additional network call
        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));


        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(1);

        // after clearing the memory another call should be made
        store.clear(barcode);
        store.get(barcode).test().awaitTerminalEvent();
        verify(persister).clear(barcode);
        assertThat(networkCalls.intValue()).isEqualTo(2);
    }

    @Test
    public void testClearAllBarCodes() {
        BarCode barcode1 = new BarCode("type1", "key1");
        BarCode barcode2 = new BarCode("type2", "key2");

        when(persister.read(barcode1))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call
        when(persister.write(barcode1, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode1, 2)).thenReturn(Observable.just(true));

        when(persister.read(barcode2))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call

        when(persister.write(barcode2, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode2, 2)).thenReturn(Observable.just(true));


        // each request should produce one call
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(2);

        store.clear();

        // after everything is cleared each request should produce another 2 calls
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(4);
    }
}
