package com.nytimes.android.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.nytimes.android.store.base.Store;
import com.nytimes.android.store.base.impl.StoreBuilder;

import static com.nytimes.android.store.base.impl.BarCode.empty;

public class StoreActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Store<String> sampleStore = StoreBuilder.<String>builder()
                .nonObservableFetcher(barCode -> "Hello")
                .open();

        sampleStore.get(empty()).subscribe(hello -> {
            Toast.makeText(this,hello,Toast.LENGTH_SHORT).show();
        });
    }
}
