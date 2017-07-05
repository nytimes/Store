package com.nytimes.android.external.store;

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

import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.observers.AssertableSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetRefreshingErrorTest {
    @Mock
    Persister<Integer, BarCode> persister;
    AtomicInteger networkCalls;
    private Store<Integer, BarCode> store;
    private Store<Integer, BarCode> storeWithPersister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        networkCalls = new AtomicInteger(0);
        store = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> Observable.fromCallable(() -> {
                    int i= networkCalls.incrementAndGet();
                    // forcing exception at the 3rd and 4th call made by fetcher
                    if (i == 3 || i == 4) {
                        throw new RuntimeException("Network error");
                    } else {
                        return i;
                    }
                }))
                .open();

        storeWithPersister = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> Observable.fromCallable(() -> {
                    int i= networkCalls.incrementAndGet();
                    if (i == 3 || i == 4) {
                        throw new RuntimeException("Network error");
                    } else {
                        return i;
                    }
                }))
                .persister(persister)
                .open();
    }

    @Test
    public void testRefreshOnErrorWithRetry() {
        BarCode barcode = new BarCode("type", "key");

        Observable<Integer> integerObservable = store
                .getRefreshing(barcode)
                .doOnError(e -> System.out.println(e.getLocalizedMessage()))
                .retry();

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable
                .test();

        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable
                .test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(3);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(3);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(4);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(4);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(5);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(5);

    }

    @Test
    public void testRefreshOnErrorNoRetry() {
        BarCode barcode = new BarCode("type", "key");

        Observable<Integer> integerObservable = store
                .getRefreshing(barcode);

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable.test();
        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable.test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);
    }

    @Test
    public void testGetRefreshinErrorWithRetryAndPersister() {
        BarCode barcode = new BarCode("type", "key");
        when(persister.read(barcode))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)) //read from disk after making additional network call
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call

        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));

        Observable<Integer> integerObservable = store
                .getRefreshing(barcode)
                .retry();

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable.test();
        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable.test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(3);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(3);

    }

    @Test
    public void testGetRefreshinErrorWithoutRetryAndPersister() {
        BarCode barcode = new BarCode("type", "key");
        when(persister.read(barcode))
            .thenReturn(Observable.<Integer>empty()) //read from disk
            .thenReturn(Observable.just(1)) //read from disk after fetching from network
            .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
            .thenReturn(Observable.just(1)) //read from disk after making additional network call
            .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
            .thenReturn(Observable.just(1)); //read from disk after making additional network call

        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));

        Observable<Integer> integerObservable = store
                .getRefreshing(barcode);

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable.test();
        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable.test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        store.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

    }

}
