package com.nytimes.android.external.fs;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;
import com.nytimes.android.external.store.middleware.GsonSourceParser;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;

import okio.BufferedSource;
import okio.Okio;
import rx.Observable;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SourceFilerReaderWriterStoreTest {
    public static final String KEY = "key";
    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;
    @Mock
    SourceFileReader fileReader;
    @Mock
    SourceFileWriter fileWriter;
    private final BarCode barCode = new BarCode("value", KEY);


    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }

    @Test
    public void testSimple() {
        MockitoAnnotations.initMocks(this);
        GsonSourceParser<Foo> parser = new GsonSourceParser<>(new Gson(), Foo.class);
        Store<Foo, BarCode> simpleStore = StoreBuilder.<BarCode, BufferedSource, Foo>parsedWithKey()
                .persister(fileReader, fileWriter)
                .fetcher(fetcher)
                .parser(parser)
                .open();

        Foo foo = new Foo();
        foo.bar = barCode.getKey();

        String sourceData = new Gson().toJson(foo);

        BufferedSource source = source(sourceData);
        Observable<BufferedSource> value = Observable.just(source);
        when(fetcher.fetch(barCode))
                .thenReturn(value);

        when(fileReader.read(barCode))
                .thenReturn(Observable.<BufferedSource>empty())
                .thenReturn(value);

        when(fileWriter.write(barCode, source))
                .thenReturn(Observable.just(true));

        Foo result = simpleStore.get(barCode).toBlocking().first();
        assertThat(result.bar).isEqualTo(KEY);
        result = simpleStore.get(barCode).toBlocking().first();
        assertThat(result.bar).isEqualTo(KEY);
        verify(fetcher, times(1)).fetch(barCode);
    }

    private static class Foo {
        String bar;

        Foo() {
        }
    }

}
