package com.nytimes.android.external.store;

import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ProxyStore;


public class SampleParsingStore extends ProxyStore<String> {

    public SampleParsingStore(Fetcher<String, BarCode> fetcher,
                              Persister<String, BarCode> persister,
                              Parser<String, String> parser) {
        super(fetcher, persister, parser);
    }
}
