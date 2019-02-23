package com.nytimes.android.external.store3.base

import com.nytimes.android.external.store3.util.ParserException

//just a marker interface allowing for a reimplementation of how the parser is implemented
interface Parser<Raw, Parsed> {

    @Throws(ParserException::class)
    suspend fun apply(raw: Raw): Parsed

}
