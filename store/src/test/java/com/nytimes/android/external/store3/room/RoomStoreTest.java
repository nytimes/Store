package com.nytimes.android.external.store3.room;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.StalePolicy;
import com.nytimes.android.external.store3.base.impl.room.RoomInternalStore;
import com.nytimes.android.external.store3.base.impl.room.RoomStore;
import com.nytimes.android.external.store3.base.room.RoomPersister;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoomStoreTest {

    private static final String DISK = "disk";
    private static final String NETWORK = "fetch";
    final AtomicInteger counter = new AtomicInteger(0);
    @Mock
    Fetcher<String, BarCode> fetcher;
    @Mock
    RoomPersister<String, String, BarCode> persister;
    private final BarCode barCode = new BarCode("key", "value");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSimple() {

        RoomStore<String, BarCode> simpleStore = new RoomInternalStore<>(
                fetcher,
                persister,
                StalePolicy.UNSPECIFIED
        );


        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Observable.<String>empty())
                .thenReturn(Observable.just(DISK));


        String value = simpleStore.get(barCode).blockingFirst();

        assertThat(value).isEqualTo(DISK);
        value = simpleStore.get(barCode).blockingFirst();
        assertThat(value).isEqualTo(DISK);
        verify(fetcher, times(1)).fetch(barCode);
    }


    @Test
    public void testDoubleTap() {


        RoomStore<String, BarCode> simpleStore = new RoomInternalStore<>(
                fetcher,
                persister,
                StalePolicy.UNSPECIFIED
        );

        Single<String> networkSingle =
                Single.create(emitter -> {
                    if (counter.incrementAndGet() == 1) {
                        emitter.onSuccess(NETWORK);
                    } else {
                        emitter.onError(new RuntimeException("Yo Dawg your inflight is broken"));
                    }
                });


        when(fetcher.fetch(barCode))
                .thenReturn(networkSingle);

        when(persister.read(barCode))
                .thenReturn(Observable.empty())
                .thenReturn(Observable.just(DISK));


        String response = simpleStore.get(barCode)
                .zipWith(simpleStore.get(barCode), (s, s2) -> "hello")
                .blockingFirst();
        assertThat(response).isEqualTo("hello");
        verify(fetcher, times(1)).fetch(barCode);
    }
}
