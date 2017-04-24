package com.nytimes.android.external.store2;


import com.nytimes.android.external.store2.base.Fetcher;
import com.nytimes.android.external.store2.base.Parser;
import com.nytimes.android.external.store2.base.Persister;
import com.nytimes.android.external.store2.base.impl.BarCode;
import com.nytimes.android.external.store2.base.impl.Store;
import com.nytimes.android.external.store2.base.impl.StoreBuilder;

import org.junit.Test;

import java.util.Date;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreBuilderTest {

    public static final Date DATE = new Date();

    @Test
    public void testBuildersBuildWithCorrectTypes() {
        //test  is checking whether types are correct in builders
        Store<Date, Integer> store = StoreBuilder.<Integer, String, Date>parsedWithKey()
                .fetcher(new Fetcher<String, Integer>() {
                    @Nonnull
                    @Override
                    public Single<String> fetch(@Nonnull Integer key) {
                        return Single.just(String.valueOf(key));
                    }
                })
                .persister(new Persister<String, Integer>() {
                    @Nonnull
                    @Override
                    public Maybe<String> read(@Nonnull Integer key) {
                        return Maybe.just(String.valueOf(key));
                    }

                    @Nonnull
                    @Override
                    public Single<Boolean> write(@Nonnull Integer key, @Nonnull String s) {
                        return Single.just(true);
                    }
                })
                .parser(new Parser<String, Date>() {
                    @Override
                    public Date apply(@NonNull String s) {
                        return DATE;
                    }
                })
                .open();


        Store<Date, BarCode> barCodeStore = StoreBuilder.<Date>barcode().fetcher(new Fetcher<Date, BarCode>() {
            @Nonnull
            @Override
            public Single<Date> fetch(@Nonnull BarCode barCode) {
                return Single.just(DATE);
            }
        }).open();


        Store<Date, Integer> keyStore = StoreBuilder.<Integer, Date>key()
                .fetcher(new Fetcher<Date, Integer>() {
                    @Nonnull
                    @Override
                    public Single<Date> fetch(@Nonnull Integer key) {
                        return Single.just(DATE);
                    }
                })
                .open();
        Date result = store.get(5).blockingGet();
        result = barCodeStore.get(new BarCode("test", "5")).blockingGet();
        result = keyStore.get(5).blockingGet();
        assertThat(result).isNotNull();

    }
}
