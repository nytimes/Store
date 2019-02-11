package com.nytimes.android.external.store3;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StreamOneKeyTest {

    private static final String TEST_ITEM = "test";
    private static final String TEST_ITEM2 = "test2";

    @Mock
    Fetcher<String, BarCode> fetcher;
    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("key", "value");
    private final BarCode barCode2 = new BarCode("key2", "value2");

    private Store<String, BarCode> store;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        store = StoreBuilder.<String>barcode()
                .persister(persister)
                .fetcher(fetcher)
                .open();

        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(TEST_ITEM))
                .thenReturn(Single.just(TEST_ITEM2));

        when(persister.read(barCode))
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(TEST_ITEM))
                .thenReturn(Maybe.just(TEST_ITEM2));

        when(persister.write(barCode, TEST_ITEM))
                .thenReturn(Single.just(true));
        when(persister.write(barCode, TEST_ITEM2))
                .thenReturn(Single.just(true));
    }

    @Test
    public void testStream() {
        TestObserver<String> streamObservable = store.stream(barCode).test();
        //first time we subscribe to stream it will fail getting from memory & disk and instead
        //fresh from network, write to disk and notifiy subscribers
        streamObservable.assertValueCount(1);

        store.clear();
        //fresh should notify subscribers again
        store.fresh(barCode).test().awaitCount(1);
        streamObservable.assertValues(TEST_ITEM, TEST_ITEM2);

        //get for another barcode should not trigger a stream for barcode1
        when(fetcher.fetch(barCode2))
                .thenReturn(Single.just(TEST_ITEM));
        when(persister.read(barCode2))
                .thenReturn(Maybe.empty())
                .thenReturn(Maybe.just(TEST_ITEM));
        when(persister.write(barCode2, TEST_ITEM))
                .thenReturn(Single.just(true));
        store.get(barCode2).test().awaitCount(1);
        streamObservable.assertValueCount(2);
    }
}
