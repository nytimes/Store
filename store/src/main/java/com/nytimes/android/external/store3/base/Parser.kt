package com.nytimes.android.external.store3.base

import com.nytimes.android.external.store3.util.ParserException

import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function

//just a marker interface allowing for a reimplementation of how the parser is implemented
interface Parser<Raw, Parsed>  {

    @Throws(ParserException::class)
    suspend fun apply(@NonNull raw: Raw): Parsed

}
