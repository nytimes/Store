package com.nytimes.android.external.store2;

import com.nytimes.android.external.store2.base.impl.BarCode;
import com.nytimes.android.external.store2.base.impl.Store;
import com.nytimes.android.external.store2.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Single;


public class DontCacheErrorsTest {

    boolean shouldThrow;
    private Store<Integer, BarCode> store;

    @Before
    public void setUp() {
        store = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> Single.fromCallable(() -> {
                    if (shouldThrow) {
                        throw new RuntimeException();
                    } else {
                        return 0;
                    }
                }))
                .open();
    }

    @Test
    public void testStoreDoesntCacheErrors() throws InterruptedException {
        BarCode barcode = new BarCode("bar", "code");

        shouldThrow = true;
        store.get(barcode).test()
                .assertTerminated()
                .assertError(Exception.class)
                .awaitTerminalEvent();

        shouldThrow = false;
        store.get(barcode).test()
                .assertNoErrors()
                .awaitTerminalEvent();
    }
}
