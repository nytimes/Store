package com.nytimes.android.external.store3.room;

import com.nytimes.android.external.store3.base.Clearable;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.StalePolicy;
import com.nytimes.android.external.store3.base.impl.room.StoreRoom;
import com.nytimes.android.external.store3.base.room.RoomPersister;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClearStoreRoomTest {
    @Mock
    RoomClearingPersister persister;
    private AtomicInteger networkCalls;
    private StoreRoom<Integer, BarCode> store;

    @Before
    public void setUp() {
        networkCalls = new AtomicInteger(0);
        store = StoreRoom.from(
                barCode -> Single.fromCallable(() -> networkCalls.incrementAndGet()),
                persister,
                StalePolicy.UNSPECIFIED);
    }

    @Test
    public void testClearSingleBarCode() {
        // one request should produce one call
        BarCode barcode = new BarCode("type", "key");

        when(persister.read(barcode))
                .thenReturn(Observable.empty()) //read from disk on get
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.empty()) //read from disk after clearing
                .thenReturn(Observable.just(1)); //read from disk after making additional network call

        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(1);

        // after clearing the memory another call should be made
        store.clear(barcode);
        store.get(barcode).test().awaitTerminalEvent();
        verify(persister).clear(barcode);
        assertThat(networkCalls.intValue()).isEqualTo(2);
    }

    @Test
    public void testClearAllBarCodes() {
        BarCode barcode1 = new BarCode("type1", "key1");
        BarCode barcode2 = new BarCode("type2", "key2");

        when(persister.read(barcode1))
                .thenReturn(Observable.empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call

        when(persister.read(barcode2))
                .thenReturn(Observable.empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)); //read from disk after making additional network call


        // each request should produce one call
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(2);

        store.clear();

        // after everything is cleared each request should produce another 2 calls
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(4);
    }

    //everything will be mocked
    static class RoomClearingPersister implements RoomPersister<Integer, Integer, BarCode>, Clearable<BarCode> {
        @Override
        public void clear(@Nonnull BarCode key) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Observable<Integer> read(@Nonnull BarCode barCode) {
            throw new RuntimeException();
        }

        @Override
        public void write(@Nonnull BarCode barCode, @Nonnull Integer integer) {
            //noop
        }
    }
}
