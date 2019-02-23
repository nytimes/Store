package com.nytimes.android.external.store3.util

/**
 * Exception thrown when one of the provided parsers fails.
 */
class ParserException : RuntimeException {

    constructor(cause: Throwable) : super(cause)

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
