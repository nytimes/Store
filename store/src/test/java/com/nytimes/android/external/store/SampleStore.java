package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ProxyStore;


public class SampleStore extends ProxyStore<String> {
    public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}
