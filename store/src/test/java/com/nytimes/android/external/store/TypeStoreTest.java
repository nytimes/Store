package com.nytimes.android.external.store;


import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.beta.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Date;

import javax.annotation.Nonnull;

import rx.Observable;

public class TypeStoreTest {

    public static final Date DATE = new Date();

    @Test
    public void simpleTest() {
        Store<Date, Integer> store = StoreBuilder
                .fromTypes(Integer.class, String.class, Date.class)
                .fetcher(new Fetcher<String, Integer>() {
                    @Nonnull
                    @Override
                    public Observable<String> fetch(Integer barCode) {
                        return Observable.just(String.valueOf(barCode));
                    }
                })
                .persister(new Persister<String, Integer>() {
                    @Nonnull
                    @Override
                    public Observable<String> read(Integer barCode) {
                        return Observable.just(String.valueOf(barCode));
                    }

                    @Nonnull
                    @Override
                    public Observable<Boolean> write(Integer barCode, String s) {
                        return Observable.empty();
                    }
                })
                .parser(new Parser<String, Date>() {
                    @Override
                    public Date call(String s) {
                        return DATE;
                    }
                })
                .open();

        Date result = store.get(5).toBlocking().first();
        Assertions.assertThat(result).isEqualTo(DATE);

    }
}
