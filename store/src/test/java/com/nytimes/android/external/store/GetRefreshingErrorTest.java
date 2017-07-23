package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Clearable;
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

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.observers.AssertableSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetRefreshingErrorTest {

    @Mock
    ClearingPersister persister;
    AtomicInteger networkCalls;
    AtomicInteger persisterCalls;
    private Store<Integer, BarCode> store;
    private Store<Integer, BarCode> storeWithPersister;
    private Store<Integer, BarCode> storeWithPersisterNBS;
    private Store<Integer, BarCode> storeWithPersisterROS;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persisterCalls = new AtomicInteger(0);
        networkCalls = new AtomicInteger(0);

        Observable<Integer> fetcherObservable = Observable.fromCallable(() -> {
            int i = networkCalls.incrementAndGet();
            // forcing exception at the 3rd and 4th call made by fetcher
            if (i == 3 || i == 4) {
                throw new RuntimeException("Network error");
            } else {
                return i;
            }
        });

        store = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> fetcherObservable)
                .open();

        storeWithPersister = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> fetcherObservable)
                .persister(persister)
                .open();

        storeWithPersisterNBS = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> fetcherObservable)
                .persister(persister)
                .networkBeforeStale()
                .open();

        storeWithPersisterROS = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> fetcherObservable)
                .persister(persister)
                .refreshOnStale()
                .open();
    }

    @Test
    public void testRefreshOnErrorWithRetry() {
        BarCode barcode = new BarCode("type", "key");

        Observable<Integer> integerObservable = store
                .getRefreshing(barcode)
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
                .thenReturn(Observable.just(2)) //read from disk after making additional network call
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(3)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(4)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(5)); //read from disk after fetching from network

        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 3)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 4)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 5)).thenReturn(Observable.just(true));

        Observable<Integer> integerObservable = storeWithPersister
                .getRefreshing(barcode)
                .retry();

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable.test();
        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable.test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        storeWithPersister.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        storeWithPersister.clear(barcode);
        //Even we have calls 3 and 4 throwing an error we called onNext 3 times.
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
            .thenReturn(Observable.just(2)) //read from disk after making additional network call
            .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
            .thenReturn(Observable.just(3)) //read from disk after making additional network call
            .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
            .thenReturn(Observable.just(4)) //read from disk after making additional network call
            .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
            .thenReturn(Observable.just(5)); //read from disk after making additional network call

        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 3)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 4)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 5)).thenReturn(Observable.just(true));

        Observable<Integer> integerObservable = storeWithPersister
                .getRefreshing(barcode);

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable.test();
        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable.test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        storeWithPersister.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        storeWithPersister.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

    }

    @Test
    public void testGetRefreshinErrorWithRetryAndPersisterRefreshOnStale() {
        BarCode barcode = new BarCode("type", "key");
        when(persister.read(barcode))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(2)) //read from disk after making additional network call
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(3)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(4)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(5)); //read from disk after fetching from network

        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 3)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 4)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 5)).thenReturn(Observable.just(true));

        Observable<Integer> integerObservable = storeWithPersisterROS
                .getRefreshing(barcode)
                .retry();

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable.test();
        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable.test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        storeWithPersisterROS.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        storeWithPersisterROS.clear(barcode);
        //Even we have calls 3 and 4 throwing an error we called onNext 3 times.
        assertThat(refreshingObservable1.getValueCount()).isEqualTo(3);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(3);

    }

    @Test
    public void testGetRefreshinErrorWithRetryAndPersisterNetworkBeforeStale() {
        BarCode barcode = new BarCode("type", "key");
        when(persister.read(barcode))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(2)) //read from disk after making additional network call
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(3)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(4)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(5)); //read from disk after fetching from network

        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 3)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 4)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 5)).thenReturn(Observable.just(true));

        Observable<Integer> integerObservable = storeWithPersisterNBS
                .getRefreshing(barcode)
                .retry();

        AssertableSubscriber<Integer> refreshingObservable1 = integerObservable.test();
        AssertableSubscriber<Integer> refreshingObservable2 = integerObservable.test();

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(1);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(1);

        storeWithPersisterNBS.clear(barcode);

        assertThat(refreshingObservable1.getValueCount()).isEqualTo(2);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(2);

        storeWithPersisterNBS.clear(barcode);
        //Even we have calls 3 and 4 throwing an error we called onNext 3 times.
        assertThat(refreshingObservable1.getValueCount()).isEqualTo(3);
        assertThat(refreshingObservable2.getValueCount()).isEqualTo(3);

    }


    //everything will be mocked
    class ClearingPersister implements Persister<Integer, BarCode>, Clearable<BarCode> {
        @Override
        public void clear(@Nonnull BarCode key) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Observable<Integer> read(@Nonnull BarCode barCode) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Observable<Boolean> write(@Nonnull BarCode barCode, @Nonnull Integer integer) {
            throw new RuntimeException();
        }
    }

}
