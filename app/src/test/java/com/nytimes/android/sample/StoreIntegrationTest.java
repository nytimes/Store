package com.nytimes.android.sample;

import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class StoreIntegrationTest {

    private Store<String> testStore;

    @Test
    public void addition_isCorrect() throws Exception {

    }


    @Before
    public void setUp() throws Exception {
        testStore = StoreBuilder.<String>builder()
                .nonObservableFetcher(barCode -> "hello")
                .open();

    }

    @Test
    public void testRepeatedGet() throws Exception {
        String first = testStore.get(BarCode.empty()).toBlocking().first();
        assertEquals(first,"hello");

    }
}
