package com.nytimes.android.external.store3.middleware

import com.google.gson.Gson
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.util.ParserException
import io.reactivex.annotations.NonNull
import java.io.Reader
import java.lang.reflect.Type
import javax.inject.Inject

class GsonReaderParser<Parsed> @Inject
constructor(private val gson: Gson, private val type: Type) : Parser<Reader, Parsed> {

    @Throws(ParserException::class)
    override fun apply(@NonNull reader: Reader): Parsed = gson.fromJson(reader, type)
}
