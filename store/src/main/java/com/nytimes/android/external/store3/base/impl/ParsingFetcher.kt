package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser


/**
 * Parsing fetcher that takes parser of Raw type and fetcher of raw type returning parsed instance.
 * Created on 10/20/17.
 */
class ParsingFetcher<Parsed, Raw, Key>
(private val rawFetcher: Fetcher<Raw, Key>,
 private val parser: Parser<Raw, Parsed>) : Fetcher<Parsed, Key> {

    override suspend fun fetch(key: Key): Parsed {
        return rawFetcher.fetch(key).let { parser.apply(it) }
    }

    companion object {

        /**
         * Creates ParsingFetcher for raw data type Fetcher and Raw data Parser.
         */
        fun <Parsed, Raw, Key> from(
                fetcher: Fetcher<Raw, Key>, parser: Parser<Raw, Parsed>): ParsingFetcher<Parsed, Raw, Key> {
            return ParsingFetcher(fetcher, parser)
        }
    }
}
