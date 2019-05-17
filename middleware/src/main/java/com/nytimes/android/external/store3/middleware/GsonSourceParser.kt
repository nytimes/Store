package com.nytimes.android.external.store3.middleware


import com.google.gson.Gson
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.util.ParserException
import okio.BufferedSource
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.nio.charset.Charset
import javax.inject.Inject


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

    @Throws(ParserException::class)
    override suspend fun apply(raw: BufferedSource): Parsed {
        try {
            InputStreamReader(raw.inputStream(), Charset.forName("UTF-8")).use { reader -> return gson.fromJson(reader, type) }
        } catch (e: IOException) {
            throw ParserException(e?.message ?: "", e)
        }

    }
}
