package com.nytimes.android.sample;

import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Single;

import static junit.framework.Assert.assertEquals;

public class StoreIntegrationTest {

    private Store<String, BarCode> testStore;

    @Before
    public void setUp() throws Exception {
        testStore = StoreBuilder.<String>barcode()
                .fetcher(barCode -> Single.just("hello"))
                .open();
    }

    @Test
    public void testRepeatedGet() throws Exception {
        String first = testStore.get(BarCode.empty()).blockingGet();
        assertEquals(first, "hello");

    }
}
