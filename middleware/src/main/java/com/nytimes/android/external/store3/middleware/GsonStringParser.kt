package com.nytimes.android.external.store3.middleware

import com.google.gson.Gson
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.util.ParserException

import java.lang.reflect.Type

import javax.inject.Inject

import io.reactivex.annotations.NonNull

import com.nytimes.android.external.cache3.Preconditions.checkNotNull

class GsonStringParser<Parsed> @Inject
constructor(private val gson: Gson, private val type: Type) : Parser<String, Parsed> {

    init {
        checkNotNull(gson, "Gson can't be null")
        checkNotNull(type, "Type can't be null")
    }

    @Throws(ParserException::class)
    override fun apply(@NonNull s: String): Parsed? {
        return gson.fromJson<Parsed>(s, type)
    }
}
