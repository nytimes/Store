package com.nytimes.android.external.store2.util;

/**
 * Exception thrown when one of the provided parsers fails.
 */
public class ParserException extends RuntimeException {

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
