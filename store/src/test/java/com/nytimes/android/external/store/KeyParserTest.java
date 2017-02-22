package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;
import com.nytimes.android.external.store.util.KeyParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.observers.AssertableSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class KeyParserTest {

    public static final String NETWORK = "Network";
    public static final int KEY = 5;
    private Store<String, Integer> store;

    @Before
    public void setUp() throws Exception {
        store = StoreBuilder.<Integer, String, String>parsedWithKey()
                .parser(new KeyParser<Integer, String, String>() {
                    @Override
                    public String call(Integer integer, String s) {
                        return s + integer;
                    }
                })
                .fetcher(new Fetcher<String, Integer>() {
                    @Nonnull
                    @Override
                    public Observable<String> fetch(@Nonnull Integer integer) {
                        return Observable.just(NETWORK);
                    }
                }).open();

    }

    @Test
    public void testStoreWithKeyParserFuncNoPersister() throws Exception {
        AssertableSubscriber<String> testObservable = store.get(KEY).test().awaitTerminalEvent();
        testObservable.assertNoErrors()
                .assertValues(NETWORK + KEY)
                .assertUnsubscribed();


    }
}
