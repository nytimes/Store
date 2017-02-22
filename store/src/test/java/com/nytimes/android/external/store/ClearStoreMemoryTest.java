package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class ClearStoreMemoryTest {

    int networkCalls = 0;
    private Store<Integer, BarCode> store;

    @Before
    public void setUp() {
        networkCalls = 0;
        store = StoreBuilder.<Integer>barcode()
                .fetcher(new Fetcher<Integer, BarCode>() {
                    @Nonnull
                    @Override
                    public Observable<Integer> fetch(@Nonnull BarCode barCode) {
                        return Observable.fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() {
                                return networkCalls++;
                            }
                        });
                    }
                })
                .open();
    }

    @Test
    public void testClearSingleBarCode() {
        //one request should produce one call
        BarCode barcode = new BarCode("type", "key");
        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(1);

        // after clearing the memory another call should be made
        store.clearMemory(barcode);
        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(2);
    }

    @Test
    public void testClearAllBarCodes() {
        BarCode b1 = new BarCode("type1", "key1");
        BarCode b2 = new BarCode("type2", "key2");

        //each request should produce one call
        store.get(b1).test().awaitTerminalEvent();
        store.get(b2).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(2);

        store.clearMemory();

        //after everything is cleared each request should produce another 2 calls
        store.get(b1).test().awaitTerminalEvent();
        store.get(b2).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(4);
    }
}
