package com.nytimes.android.sample;

import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;


import io.reactivex.Observable;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class StoreIntegrationTest {

    private Store<String, BarCode> testStore;

    @Test
    public void addition_isCorrect() throws Exception {

    }


    @Before
    public void setUp() throws Exception {
        testStore = StoreBuilder.<String>barcode()
                .fetcher(barCode -> Observable.just("hello"))
                .open();

    }

    @Test
    public void testRepeatedGet() throws Exception {
        String first = testStore.get(BarCode.empty()).blockingFirst();
        assertEquals(first, "hello");

    }
}
