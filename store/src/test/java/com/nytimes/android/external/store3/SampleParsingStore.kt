package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.RealStore


class SampleParsingStore(fetcher: Fetcher<String, BarCode>,
                         persister: Persister<String, BarCode>,
                         parser: Parser<String, String>
) : RealStore<String, BarCode>(fetcher, persister, parser)
