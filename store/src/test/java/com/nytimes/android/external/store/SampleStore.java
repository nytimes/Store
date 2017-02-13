package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.RealStore;


public class SampleStore extends RealStore<String, BarCode> {
    public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}
