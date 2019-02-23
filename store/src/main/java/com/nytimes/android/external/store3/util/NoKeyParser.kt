package com.nytimes.android.external.store3.util

import com.nytimes.android.external.store3.base.Parser

class NoKeyParser<in Key, in Raw, out Parsed>(private val parser: Parser<Raw, out Parsed>) : KeyParser<Key, Raw, Parsed> {

    @Throws(ParserException::class)
    override suspend fun apply(key: Key, raw: Raw): Parsed {
        return parser.apply(raw)
    }
}
