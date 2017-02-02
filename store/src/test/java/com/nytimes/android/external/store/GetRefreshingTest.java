package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.observers.AssertableSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

public class GetRefreshingTest {

    private int networkCalls = 0;
    private Store<Integer> store;

    @Before
    public void setUp() {
        networkCalls = 0;
        store = StoreBuilder.<Integer>builder()
                .fetcher(new Fetcher<Integer>() {
                    @Nonnull
                    @Override
                    public Observable<Integer> fetch(BarCode barCode) {
                        return Observable.fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() {
                                return networkCalls++;
                            }
                        });
                    }
                })
                .open();
    }

    @Test
    public void testRefreshOnClear() {
        BarCode barcode = new BarCode("type", "key");
        AssertableSubscriber<Integer> testStore = store.getRefreshing(barcode).test();
        assertThat(testStore.getValueCount()).isEqualTo(1);
        assertThat(networkCalls).isEqualTo(1);
        //clearing the store should produce another network call
        store.clearMemory(barcode);
        assertThat(testStore.getValueCount()).isEqualTo(2);
        assertThat(networkCalls).isEqualTo(2);

        store.get(barcode).test().awaitTerminalEvent();
        assertThat(testStore.getValueCount()).isEqualTo(2);
        assertThat(networkCalls).isEqualTo(2);
    }

    @Test
    public void testRefreshOnClearAll(){
        BarCode barcode1 = new BarCode("type", "key");
        BarCode barcode2 = new BarCode("type", "key2");

        AssertableSubscriber<Integer> testObservable1 = store.getRefreshing(barcode1).test();
        AssertableSubscriber<Integer> testObservable2 = store.getRefreshing(barcode2).test();
        assertThat(testObservable1.getValueCount()).isEqualTo(1);
        assertThat(testObservable2.getValueCount()).isEqualTo(1);

        assertThat(networkCalls).isEqualTo(2);

        store.clearMemory();
        assertThat(networkCalls).isEqualTo(4);


    }

}
