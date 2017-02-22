package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Clearable;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.observers.AssertableSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetRefreshingTest {
    @Mock
    ClearingPersister persister;
    AtomicInteger networkCalls;
    private Store<Integer, BarCode> store;

    @Before
    public void setUp() {
        networkCalls = new AtomicInteger(0);
        store = StoreBuilder.<Integer>barcode()
                .fetcher(new Fetcher<Integer, BarCode>() {
                    @Nonnull
                    @Override
                    public Observable<Integer> fetch(@Nonnull BarCode barCode) {
                        return Observable.fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() {
                                return networkCalls.incrementAndGet();
                            }
                        });
                    }
                })
                .persister(persister)
                .open();
    }

    @Test
    public void testRefreshOnClear() {
        BarCode barcode = new BarCode("type", "key");
        when(persister.read(barcode))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call
        when(persister.write(barcode, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode, 2)).thenReturn(Observable.just(true));


        AssertableSubscriber<Integer> refreshingObservable = store.getRefreshing(barcode).test();
        assertThat(refreshingObservable.getValueCount()).isEqualTo(1);
        assertThat(networkCalls.intValue()).isEqualTo(1);
        //clearing the store should produce another network call
        store.clear(barcode);
        assertThat(refreshingObservable.getValueCount()).isEqualTo(2);
        assertThat(networkCalls.intValue()).isEqualTo(2);

        store.get(barcode).test().awaitTerminalEvent();
        assertThat(refreshingObservable.getValueCount()).isEqualTo(2);
        assertThat(networkCalls.intValue()).isEqualTo(2);
    }

    @Test
    public void testRefreshOnClearAll() {
        BarCode barcode1 = new BarCode("type", "key");
        BarCode barcode2 = new BarCode("type", "key2");

        when(persister.read(barcode1))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call
        when(persister.write(barcode1, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode1, 2)).thenReturn(Observable.just(true));

        when(persister.read(barcode2))
                .thenReturn(Observable.<Integer>empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.<Integer>empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call

        when(persister.write(barcode2, 1)).thenReturn(Observable.just(true));
        when(persister.write(barcode2, 2)).thenReturn(Observable.just(true));

        AssertableSubscriber<Integer> testObservable1 = store.getRefreshing(barcode1).test();
        AssertableSubscriber<Integer> testObservable2 = store.getRefreshing(barcode2).test();
        assertThat(testObservable1.getValueCount()).isEqualTo(1);
        assertThat(testObservable2.getValueCount()).isEqualTo(1);

        assertThat(networkCalls.intValue()).isEqualTo(2);

        store.clear();
        assertThat(networkCalls.intValue()).isEqualTo(4);


    }

    //everything will be mocked
    static class ClearingPersister implements Persister<Integer, BarCode>, Clearable<BarCode> {
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
