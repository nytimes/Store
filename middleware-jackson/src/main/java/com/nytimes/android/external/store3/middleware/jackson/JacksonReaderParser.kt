package com.nytimes.android.external.store3.middleware.jackson


import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.util.ParserException

import java.io.IOException
import java.io.Reader
import java.lang.reflect.Type
import javax.inject.Inject

class JacksonReaderParser<Parsed> : Parser<Reader, Parsed> {

    private val objectMapper: ObjectMapper
    private val parsedType: JavaType

    constructor(jsonFactory: JsonFactory, type: Type) {
        objectMapper = ObjectMapper(jsonFactory).registerModule(KotlinModule())
        parsedType = objectMapper.constructType(type)
    }

    @Inject
    constructor(objectMapper: ObjectMapper, type: Type) {
        this.objectMapper = objectMapper
        parsedType = objectMapper.constructType(type)
    }

    @Throws(ParserException::class)
    override suspend fun apply(reader: Reader): Parsed {
        try {
            return objectMapper.readValue(reader, parsedType)
        } catch (e: IOException) {
            throw ParserException(e.message, e)
        }
    }
}
