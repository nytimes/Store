package com.nytimes.android.external.fs3;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.RecordProvider;
import com.nytimes.android.external.store3.base.RecordState;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreNetworkBeforeStaleFailTest {
    static final Exception sorry = new Exception("sorry");
    private static final BarCode barCode = new BarCode("key", "value");
    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;
    Store<BufferedSource, BarCode> store;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(new TestPersister())
                .networkBeforeStale()
                .open();

    }

    @Test
    public void networkBeforeStaleNoNetworkResponse() {
        Single<BufferedSource> exception = Single.error(sorry);
        when(fetcher.fetch(barCode))
                .thenReturn(exception);
        store.get(barCode).test().assertError(sorry);
        verify(fetcher, times(1)).fetch(barCode);
    }

    private static final class TestPersister implements Persister<BufferedSource, BarCode>, RecordProvider<BarCode> {
        @Nonnull
        @Override
        public RecordState getRecordState(@Nonnull BarCode barCode) {
            return RecordState.MISSING;
        }

        @Nonnull
        @Override
        public Maybe<BufferedSource> read(@Nonnull BarCode barCode) {
            return Maybe.error(sorry);
        }

        @Nonnull
        @Override
        public Single<Boolean> write(@Nonnull BarCode barCode,
                                         @Nonnull BufferedSource bufferedSource) {
            return Single.just(true);
        }
    }
}
