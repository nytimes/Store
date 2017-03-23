package com.nytimes.android.external.store2;

import com.nytimes.android.external.store2.base.Fetcher;
import com.nytimes.android.external.store2.base.impl.Store;
import com.nytimes.android.external.store2.base.impl.StoreBuilder;
import com.nytimes.android.external.store2.util.KeyParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.TestObserver;


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
                    public String apply(@NonNull Integer integer, @NonNull String s) {
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
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void testStoreWithKeyParserFuncNoPersister() throws Exception {
        TestObserver<String> testObservable = store.get(KEY).test().await();
        testObservable.assertNoErrors()
                .assertValues(NETWORK + KEY)
                .awaitTerminalEvent();
    }
}
