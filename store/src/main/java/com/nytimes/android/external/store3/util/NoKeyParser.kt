package com.nytimes.android.external.store3.util

import com.nytimes.android.external.store3.base.Parser

import io.reactivex.annotations.NonNull

class NoKeyParser<Key, Raw, Parsed>(private val parser: Parser<Raw, Parsed>) : KeyParser<Key, Raw, Parsed> {

    @Throws(ParserException::class)
    override suspend fun apply(@NonNull key: Key, @NonNull raw: Raw): Parsed {
        return parser.apply(raw)
    }
}
