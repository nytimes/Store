package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.RealStore;
import com.nytimes.android.external.store.util.NoopPersister;


public class SampleStore extends RealStore<String> {
    public SampleStore(Fetcher<String> fetcher, Persister<String> persister) {
        super(fetcher, persister);
    }
    public SampleStore(Fetcher<String> fetcher) {
        super(fetcher, new NoopPersister<String>());
    }
}
