package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.util.KeyParser
import com.nytimes.android.external.store3.util.ParserException

class MultiParser<Key, Raw, Parsed>(private val parsers: List<KeyParser<Any?, Any?, Any?>>) : KeyParser<Key, Raw, Parsed> {

    private fun createParserException(): ParserException {
        return ParserException("One of the provided parsers has a wrong typing. " +
                "Make sure that parsers are passed in a correct order and the fromTypes match each other.")
    }

    override suspend fun apply(key: Key, raw: Raw): Parsed {
        var parsed: Any = raw!!
        for (parser in parsers) {
            try {
                parsed = parser.apply(key, parsed)!!
            } catch (exception: ClassCastException) {
                throw createParserException()
            }
        }
        return parsed as Parsed
    }
}
