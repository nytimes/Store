//package com.nytimes.android.external.store3.base.impl
//
//import com.nytimes.android.external.store3.util.KeyParser
//import com.nytimes.android.external.store3.util.ParserException
//
//import java.util.ArrayList
//
//import io.reactivex.annotations.NonNull
//
//import com.nytimes.android.external.cache3.Preconditions.checkArgument
//import com.nytimes.android.external.cache3.Preconditions.checkNotNull
//
//class MultiParser<Key, Raw, Parsed>(parsers: List<KeyParser<Key, Any, Any>>) : KeyParser<Key, Raw, Parsed> {
//
//    private val parsers = ArrayList<KeyParser<*, *, *>>()
//
//    init {
//        this.parsers.addAll(parsers)
//    }
//
//    private fun createParserException(): ParserException {
//        return ParserException("One of the provided parsers has a wrong typing. " + "Make sure that parsers are passed in a correct order and the fromTypes match each other.")
//    }
//
//    @NonNull
//    @Throws(ParserException::class)
//    override
//    suspend fun apply(@NonNull key: Key, @NonNull raw: Raw): Parsed {
//        var parsed: Any = raw!!
//        for (parser in parsers) {
//            try {
//                parsed = parser.apply(key, parsed)!!
//            } catch (exception: ClassCastException) {
//                throw createParserException()
//            }
//
//        }
//        return parsed as Parsed
//    }
//}
