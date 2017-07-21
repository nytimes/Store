package com.nytimes.android.external.store3;

import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Single;

/**
 * Created by 206847 on 5/3/17.
 */

public class NoNetworkTest {

    private static final RuntimeException EXCEPTION = new RuntimeException();
    private Store<Object, BarCode> store;

    @Before
    public void setUp() {
        store = StoreBuilder.barcode()
                .fetcher(barcode -> Single.error(EXCEPTION))
                .open();
    }

    @Test
    public void testNoNetwork() throws Exception {
        store.get(new BarCode("test", "test"))
                .test()
                .assertError(EXCEPTION);
    }

    @Test
    public void testNoNetworkWithResult() throws Exception {
        store.getWithResult(new BarCode("test", "test"))
            .test()
            .assertError(EXCEPTION);
    }
}
