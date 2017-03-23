package com.nytimes.android.external.store2;

import com.nytimes.android.external.store2.base.Fetcher;
import com.nytimes.android.external.store2.base.Parser;
import com.nytimes.android.external.store2.base.Persister;
import com.nytimes.android.external.store2.base.impl.BarCode;
import com.nytimes.android.external.store2.base.impl.RealStore;


public class SampleParsingStore extends RealStore<String, BarCode> {

    public SampleParsingStore(Fetcher<String, BarCode> fetcher,
                              Persister<String, BarCode> persister,
                              Parser<String, String> parser) {
        super(fetcher, persister, parser);
    }
}
