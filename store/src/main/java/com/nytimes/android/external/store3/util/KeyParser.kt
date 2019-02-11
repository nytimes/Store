package com.nytimes.android.external.store3.util

import io.reactivex.annotations.NonNull

interface KeyParser<in Key, in Raw, out Parsed> {

    @Throws(ParserException::class)
    suspend fun apply(@NonNull key: Key, @NonNull raw: Raw): Parsed

}
