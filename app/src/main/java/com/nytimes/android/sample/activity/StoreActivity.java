package com.nytimes.android.sample.activity;

import android.app.Activity;
import android.os.Bundle;

import com.nytimes.android.cache.Cache;
import com.nytimes.android.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AtomicInteger integer = new AtomicInteger(0);


        Cache<Object, Object> cache = CacheBuilder.newBuilder()
                .maximumSize(5)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .build();

        try {
            cache.get("hello", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return "Hello";
                }
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


//        Store<String> sampleStore = StoreBuilder.<String>builder()
//                .nonObservableFetcher(barCode -> "Hello")
//                .persister(new Persister<String>() {
//                    @Override
//                    public Observable<String> read(BarCode barCode) {
//                        return Observable.just(integer.incrementAndGet() + "");
//                    }
//
//                    @Override
//                    public Observable<Boolean> write(BarCode barCode, String s) {
//                        return Observable.empty();
//                    }
//                })
//                .open();
//
//        sampleStore.get(empty())
//                .delay(5, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(hello -> {
//                    Toast.makeText(this, hello, Toast.LENGTH_SHORT).show();
//                });
//        sampleStore.get(empty())
//                .delay(10, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(hello -> {
//                    Toast.makeText(this, hello, Toast.LENGTH_SHORT).show();
//                });
//        sampleStore.get(empty())
//                .delay(15, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(hello -> {
//                    Toast.makeText(this, hello, Toast.LENGTH_SHORT).show();
//                });
    }
}
