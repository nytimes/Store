package com.nytimes.android.external.fs3


import com.nytimes.android.external.store3.base.impl.BarCode

class BarCodeReadAllPathResolver : PathResolver<BarCode> {

    override fun resolve(barCode: BarCode): String =
            barCode.type + "/" + barCode.key
}
