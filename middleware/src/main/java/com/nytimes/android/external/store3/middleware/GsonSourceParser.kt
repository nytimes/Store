package com.nytimes.android.external.store3.middleware


import com.google.gson.Gson
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.util.ParserException

import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.nio.charset.Charset

import javax.inject.Inject

import io.reactivex.annotations.NonNull
import okio.BufferedSource

import com.nytimes.android.external.cache3.Preconditions.checkNotNull


/**
 * Parser to be used when going from a BufferedSource to any Parsed Type
 * example usage:
 * ParsingStoreBuilder.<BufferedSource></BufferedSource>, BookResults>builder()
 * .fetcher(fetcher)
 * .persister(SourcePersisterFactory.create(getApplicationContext().getCacheDir()))
 * .parser(GsonParserFactory.createSourceParser(new Gson(),BookResult.class)
 * .open();
 */


class GsonSourceParser<Parsed> @Inject
constructor(private val gson: Gson, private val type: Type) : Parser<BufferedSource, Parsed> {

    init {
        checkNotNull(gson, "Gson can't be null")
        checkNotNull(type, "Type can't be null")
    }

    @Throws(ParserException::class)
    override fun apply(@NonNull bufferedSource: BufferedSource): Parsed {
        try {
            InputStreamReader(bufferedSource.inputStream(), Charset.forName("UTF-8")).use { reader -> return gson.fromJson(reader, type) }
        } catch (e: IOException) {
            throw ParserException(e.message, e)
        }

    }
}
