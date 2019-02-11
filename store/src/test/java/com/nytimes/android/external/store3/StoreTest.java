package com.nytimes.android.external.store3;

import com.nytimes.android.external.cache3.Cache;
import com.nytimes.android.external.cache3.CacheBuilder;
import com.nytimes.android.external.store.util.Result;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.RealStore;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.util.NoopPersister;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StoreTest {

    private static final String DISK = "disk";
    private static final String NETWORK = "fresh";
    private static final String MEMORY = "memory";
    final AtomicInteger counter = new AtomicInteger(0);
    @Mock
    Fetcher<String, BarCode> fetcher;
    @Mock
    Persister<String, BarCode> persister;
    private final BarCode barCode = new BarCode("key", "value");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSimple() {

        Store<String, BarCode> simpleStore = StoreBuilder.<String>barcode()
                .persister(persister)
                .fetcher(fetcher)
                .open();


        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(DISK));

        when(persister.write(barCode, NETWORK))
                .thenReturn(Single.just(true));

        String value = simpleStore.get(barCode).blockingGet();

        assertThat(value).isEqualTo(DISK);
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(DISK);
        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testSimpleWithResult() {

        Store<String, BarCode> simpleStore = StoreBuilder.<String>barcode()
            .persister(persister)
            .fetcher(fetcher)
            .open();


        when(fetcher.fetch(barCode))
            .thenReturn(Single.just(NETWORK));

        when(persister.read(barCode))
            .thenReturn(Maybe.<String>empty())
            .thenReturn(Maybe.just(DISK));

        when(persister.write(barCode, NETWORK))
            .thenReturn(Single.just(true));

        Result<String> result = simpleStore.getWithResult(barCode).blockingGet();

        assertThat(result.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(result.value()).isEqualTo(DISK);

        result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(Result.Source.CACHE);
        assertThat(result.value()).isEqualTo(DISK);
        verify(fetcher, times(1)).fetch(barCode);
    }


    @Test
    public void testDoubleTap() {

        Store<String, BarCode> simpleStore = StoreBuilder.<String>barcode()
                .persister(persister)
                .fetcher(fetcher)
                .open();

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
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(DISK));

        when(persister.write(barCode, NETWORK))
                .thenReturn(Single.just(true));


        String response = simpleStore.get(barCode)
                .zipWith(simpleStore.get(barCode), (s, s2) -> "hello")
                .blockingGet();
        assertThat(response).isEqualTo("hello");
        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testDoubleTapWithResult() {

        Store<String, BarCode> simpleStore = StoreBuilder.<String>barcode()
            .persister(persister)
            .fetcher(fetcher)
            .open();

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
            .thenReturn(Maybe.<String>empty())
            .thenReturn(Maybe.just(DISK));

        when(persister.write(barCode, NETWORK))
            .thenReturn(Single.just(true));


        Result<String> response = simpleStore.getWithResult(barCode)
            .zipWith(simpleStore.getWithResult(barCode), (s, s2) -> Result.createFromNetwork("hello"))
            .blockingGet();

        assertThat(response.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(response.value()).isEqualTo("hello");
        verify(fetcher, times(1)).fetch(barCode);
    }


    @Test
    public void testSubclass() {

        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);
        simpleStore.clear();

        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK));

        when(persister.read(barCode))
                .thenReturn(Maybe.<String>empty())
                .thenReturn(Maybe.just(DISK));
        when(persister.write(barCode, NETWORK)).thenReturn(Single.just(true));

        String value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(DISK);
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(DISK);
        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testSubclassWithResult() {

        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);
        simpleStore.clear();

        when(fetcher.fetch(barCode))
            .thenReturn(Single.just(NETWORK));

        when(persister.read(barCode))
            .thenReturn(Maybe.<String>empty())
            .thenReturn(Maybe.just(DISK));
        when(persister.write(barCode, NETWORK)).thenReturn(Single.just(true));

        Result<String> result = simpleStore.getWithResult(barCode).blockingGet();

        assertThat(result.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(result.value()).isEqualTo(DISK);

        result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(Result.Source.CACHE);
        assertThat(result.value()).isEqualTo(DISK);
        verify(fetcher, times(1)).fetch(barCode);
    }

    @Test
    public void testNoopAndDefault() {

        Persister<String, BarCode> persister = spy(NoopPersister.Companion.<String, BarCode>create());
        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);


        when(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK));

        String value = simpleStore.get(barCode).blockingGet();
        verify(fetcher, times(1)).fetch(barCode);
        verify(persister, times(1)).write(barCode, NETWORK);
        verify(persister, times(2)).read(barCode);
        assertThat(value).isEqualTo(NETWORK);


        value = simpleStore.get(barCode).blockingGet();
        verify(persister, times(2)).read(barCode);
        verify(persister, times(1)).write(barCode, NETWORK);
        verify(fetcher, times(1)).fetch(barCode);

        assertThat(value).isEqualTo(NETWORK);
    }

    @Test
    public void testNoopAndDefaultWithResult() {

        Persister<String, BarCode> persister = spy(NoopPersister.Companion.<String, BarCode>create());
        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);


        when(fetcher.fetch(barCode))
            .thenReturn(Single.just(NETWORK));

        Result<String> value = simpleStore.getWithResult(barCode).blockingGet();
        verify(fetcher, times(1)).fetch(barCode);
        verify(persister, times(1)).write(barCode, NETWORK);
        verify(persister, times(2)).read(barCode);
        assertThat(value.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(value.value()).isEqualTo(NETWORK);


        value = simpleStore.getWithResult(barCode).blockingGet();
        verify(persister, times(2)).read(barCode);
        verify(persister, times(1)).write(barCode, NETWORK);
        verify(fetcher, times(1)).fetch(barCode);

        assertThat(value.source()).isEqualTo(Result.Source.CACHE);
        assertThat(value.value()).isEqualTo(NETWORK);
    }

    @Test
    public void testEquivalence() {
        Cache<BarCode, String> cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterAccess(Long.MAX_VALUE, TimeUnit.SECONDS)
                .build();

        cache.put(barCode, MEMORY);
        String value = cache.getIfPresent(barCode);
        assertThat(value).isEqualTo(MEMORY);

        value = cache.getIfPresent(new BarCode(barCode.getType(), barCode.getKey()));
        assertThat(value).isEqualTo(MEMORY);
    }
}
