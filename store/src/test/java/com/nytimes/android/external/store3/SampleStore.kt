package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.RealStore


class SampleStore(fetcher: Fetcher<String, BarCode>, persister: Persister<String, BarCode>) :
        RealStore<String, BarCode>(fetcher, persister)
