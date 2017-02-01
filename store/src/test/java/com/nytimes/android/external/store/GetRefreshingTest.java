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
        // one request should produce one call
        BarCode barcode = new BarCode("type", "key");
        AssertableSubscriber<Integer> testStore = store.getRefreshing(barcode).test();
        assertThat(testStore.getValueCount()).isEqualTo(1);
        assertThat(networkCalls).isEqualTo(1);
        this.store.clearMemory(barcode);

        assertThat(testStore.getValueCount()).isEqualTo(2);
        assertThat(networkCalls).isEqualTo(2);

        this.store.clearMemory(barcode);

        assertThat(testStore.getValueCount()).isEqualTo(3);
        assertThat(networkCalls).isEqualTo(3);
    }

}
