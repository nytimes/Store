package com.nytimes.android.store;

import com.nytimes.android.store.base.Fetcher;
import com.nytimes.android.store.base.Persister;
import com.nytimes.android.store.base.impl.RealStore;


public class SampleStore extends RealStore<String> {
    public SampleStore(Fetcher<String> fetcher, Persister<String> persister) {
        super(fetcher, persister);
    }
}
