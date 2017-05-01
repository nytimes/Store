package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class SequentialTest {

    int networkCalls = 0;
    private Store<Integer, BarCode> store;

    @Before
    public void setUp() {
        networkCalls = 0;
        store = StoreBuilder.<Integer>barcode()
                .fetcher(barCode -> Observable.fromCallable(() -> networkCalls++))
                .open();
    }

    @Test
    public void sequentially() {
        BarCode b = new BarCode("one", "two");
        store.get(b).test().awaitTerminalEvent();
        store.get(b).test().awaitTerminalEvent();

        assertThat(networkCalls).isEqualTo(1);
    }

    @Test
    public void parallel() {
        BarCode b = new BarCode("one", "two");
        Observable<Integer> first = store.get(b);
        Observable<Integer> second = store.get(b);

        first.test().awaitTerminalEvent();
        second.test().awaitTerminalEvent();

        assertThat(networkCalls).isEqualTo(1);
    }
}
