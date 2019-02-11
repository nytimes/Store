package com.nytimes.android.external.store3.util

import io.reactivex.annotations.NonNull
import io.reactivex.functions.BiFunction

interface KeyParser<Key, Raw, Parsed>  {

    @Throws(ParserException::class)
    suspend fun apply(@NonNull key: Key, @NonNull raw: Raw): Parsed

}
