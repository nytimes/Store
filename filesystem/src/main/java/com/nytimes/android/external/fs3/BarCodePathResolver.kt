package com.nytimes.android.external.fs3

import com.nytimes.android.external.store3.base.impl.BarCode

class BarCodePathResolver : PathResolver<BarCode> {
    override fun resolve(key: BarCode): String = key.toString()
}
