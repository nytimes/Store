package com.nytimes.android.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.StoreBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.nytimes.android.external.store.base.impl.BarCode.empty;

public class StoreActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AtomicInteger integer = new AtomicInteger(0);
        Store<String> sampleStore = StoreBuilder.<String>builder()
                .nonObservableFetcher(barCode -> "Hello")
                .persister(new Persister<String>() {
                    @Override
                    public Observable<String> read(BarCode barCode) {
                        return Observable.just(integer.incrementAndGet() + "");
                    }

                    @Override
                    public Observable<Boolean> write(BarCode barCode, String s) {
                        return Observable.empty();
                    }
                })
                .open();

        sampleStore.get(empty())
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hello -> {
                    Toast.makeText(this, hello, Toast.LENGTH_SHORT).show();
                });
        sampleStore.get(empty())
                .delay(10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hello -> {
                    Toast.makeText(this, hello, Toast.LENGTH_SHORT).show();
                });
        sampleStore.get(empty())
                .delay(15, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hello -> {
                    Toast.makeText(this, hello, Toast.LENGTH_SHORT).show();
                });
    }
}
