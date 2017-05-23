package com.nytimes.android.external.fs3;

import com.google.gson.Gson;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonSourceParser;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;
import okio.Okio;

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
        Single<BufferedSource> value = Single.just(source);
        when(fetcher.fetch(barCode))
                .thenReturn(value);

        when(fileReader.read(barCode))
                .thenReturn(Maybe.<BufferedSource>empty())
                .thenReturn(value.toMaybe());

        when(fileWriter.write(barCode, source))
                .thenReturn(Single.just(true));

        Foo result = simpleStore.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(KEY);
        result = simpleStore.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(KEY);
        verify(fetcher, times(1)).fetch(barCode);
    }

    private static class Foo {
        String bar;

        Foo() {
        }
    }

}
