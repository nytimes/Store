package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.RealStore;


public class SampleStore extends RealStore<String> {
    public SampleStore(Fetcher<String> fetcher, Persister<String> persister) {
        super(fetcher, persister);
    }
}
