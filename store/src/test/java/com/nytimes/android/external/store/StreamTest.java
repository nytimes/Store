package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.observers.AssertableSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
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
                .thenReturn(Observable.just(TEST_ITEM));

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(TEST_ITEM));

        when(persister.write(barCode, TEST_ITEM))
                .thenReturn(Observable.just(true));
    }

    @Test
    public void testStream() {
        AssertableSubscriber<String> streamObservable = store.stream().test();
        assertThat(streamObservable.getValueCount()).isEqualTo(0);
        store.get(barCode).subscribe();
        assertThat(streamObservable.getValueCount()).isEqualTo(1);
    }

    @Test
    public void testStreamEmitsOnlyFreshData() {
        store.get(barCode).subscribe();
        AssertableSubscriber<String> streamObservable = store.stream().test();
        assertThat(streamObservable.getValueCount()).isEqualTo(0);
    }
}
