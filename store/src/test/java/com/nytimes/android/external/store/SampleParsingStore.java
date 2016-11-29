package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.RealStore;


public class SampleParsingStore extends RealStore<String> {

    public SampleParsingStore(Fetcher<String> fetcher, Persister<String> persister, Parser<String, String> parser) {
        super(fetcher, persister, parser);
    }
}
