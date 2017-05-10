package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;

/**
 * Created by 206847 on 5/3/17.
 */

public class NoNetworkTest {

    private static final RuntimeException EXCEPTION = new RuntimeException();
    private Store<Object, BarCode> store;

    @Before
    public void setUp() {
        store = StoreBuilder.barcode()
                .fetcher(barcode -> Observable.error(EXCEPTION))
                .open();
    }

    @Test
    public void testNoNetwork() throws Exception {
        store.get(new BarCode("test", "test"))
                .test()
                .awaitTerminalEvent()
                .assertError(EXCEPTION);
    }
}
