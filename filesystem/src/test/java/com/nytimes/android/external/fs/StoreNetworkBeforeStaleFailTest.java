package com.nytimes.android.external.fs;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.RecordProvider;
import com.nytimes.android.external.store.base.RecordState;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;
import rx.observers.AssertableSubscriber;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreNetworkBeforeStaleFailTest {
    static final Exception sorry = new Exception("sorry");
    static final BarCode barCode = new BarCode("key", "value");
    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void networkBeforeStaleNoNetworkResponse() {
        Store store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(new TestPersister())
                .networkBeforeStale()
                .open();
        Observable<BufferedSource> exception = Observable.error(sorry);
        when(fetcher.fetch(barCode))
                .thenReturn(exception);
        AssertableSubscriber<BufferedSource> subscriber = store.get(barCode).test().awaitTerminalEvent();
        subscriber.assertError(sorry);
        verify(fetcher, times(1)).fetch(barCode);


    }

    @Test
    public void networkBeforeStaleNoOpTest() {
        Store<BufferedSource, BarCode> myStore = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .networkBeforeStale()
                .open();
        Observable<BufferedSource> exception = Observable.error(sorry);
        when(fetcher.fetch(barCode))
                .thenReturn(exception);
        AssertableSubscriber<BufferedSource> subscriber = myStore.get(barCode).test().awaitTerminalEvent();
        subscriber.assertError(sorry);
        verify(fetcher, times(1)).fetch(barCode);

    }

    final class TestPersister implements Persister<BufferedSource, BarCode>, RecordProvider<BarCode> {
        @Nonnull
        @Override
        public RecordState getRecordState(@Nonnull BarCode barCode) {
            return RecordState.MISSING;
        }

        @Nonnull
        @Override
        public Observable<BufferedSource> read(@Nonnull BarCode barCode) {
            return Observable.error(sorry);
        }

        @Nonnull
        @Override
        public Observable<Boolean> write(@Nonnull BarCode barCode,
                                         @Nonnull BufferedSource bufferedSource) {
            return Observable.just(true);
        }
    }
}
