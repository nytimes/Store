package com.nytimes.android.external.store3.util

import com.nytimes.android.external.store3.base.Parser

import io.reactivex.annotations.NonNull

/**
 * Pass-through parser for stores that parse externally
 */
class NoopParserFunc<Raw, Parsed> : Parser<Raw, Parsed> {

    @Throws(ParserException::class)
    override
    suspend fun apply(raw: Raw): Parsed {

        return raw as Parsed
    }
}
