package com.nytimes.android.external.store2;

import com.nytimes.android.external.store2.base.impl.Store;
import com.nytimes.android.external.store2.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;


@RunWith(MockitoJUnitRunner.class)
public class KeyParserTest {

    public static final String NETWORK = "Network";
    public static final int KEY = 5;
    private Store<String, Integer> store;

    @Before
    public void setUp() throws Exception {
        store = StoreBuilder.<Integer, String, String>parsedWithKey()
                .parser((integer, s) -> s + integer)
                .fetcher(integer -> Single.just(NETWORK))
                .open();

    }

    @Test
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void testStoreWithKeyParserFuncNoPersister() throws Exception {
        TestObserver<String> testObservable = store.get(KEY).test().await();
        testObservable.assertNoErrors()
                .assertValues(NETWORK + KEY)
                .awaitTerminalEvent();
    }
}
