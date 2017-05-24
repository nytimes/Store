package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Date;

import javax.annotation.Nonnull;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreBuilderTest {

    public static final Date DATE = new Date();

    @Test
    public void testBuildersBuildWithCorrectTypes() {
        //test  is checking whether types are correct in builders
        Store<Date, Integer> store = StoreBuilder.<Integer, String, Date>parsedWithKey()
                .fetcher(key -> Observable.just(String.valueOf(key)))
                .persister(new Persister<String, Integer>() {
                    @Nonnull
                    @Override
                    public Observable<String> read(@Nonnull Integer key) {
                        return Observable.just(String.valueOf(key));
                    }

                    @Nonnull
                    @Override
                    public Observable<Boolean> write(@Nonnull Integer key, @Nonnull String s) {
                        return Observable.empty();
                    }
                })
                .parser(s -> DATE)
                .open();


        Store<Date, BarCode> barCodeStore = StoreBuilder.<Date>barcode()
                .fetcher(barCode -> Observable.just(DATE))
                .open();

        Store<Date, Integer> keyStore = StoreBuilder.<Integer, Date>key()
                .fetcher(key -> Observable.just(DATE))
                .open();
        Date result = store.get(5).toBlocking().first();
        result = barCodeStore.get(new BarCode("test", "5")).toBlocking().first();
        result = keyStore.get(5).toBlocking().first();
        assertThat(result).isNotNull();

    }
}
