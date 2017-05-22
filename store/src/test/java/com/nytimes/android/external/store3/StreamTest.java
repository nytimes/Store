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
public class StreamTest {

    private static final String TEST_ITEM = "test";

    @Mock
    Fetcher<String, BarCode> fetcher;
    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("key", "value");

    private Store<String, BarCode> store;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        store = StoreBuilder.<String>barcode()
                .persister(persister)
                .fetcher(fetcher)
                .open();

        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(TEST_ITEM));

        when(persister.read(barCode))
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(TEST_ITEM));

        when(persister.write(barCode, TEST_ITEM))
                .thenReturn(Single.just(true));
    }

    @Test
    public void testStream() {
        TestObserver<String> streamObservable = store.stream().test();
        streamObservable.assertValueCount(0);
        store.get(barCode).subscribe();
        streamObservable.assertValueCount(1);
    }

    @Test
    public void testStreamEmitsOnlyFreshData() {
        store.get(barCode).subscribe();
        TestObserver<String> streamObservable = store.stream().test();
        streamObservable.assertValueCount(0);
    }
}
