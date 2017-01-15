package com.nytimes.android.external.store.util;

/**
 * Exception thrown when one of the provided parsers fails.
 */
public class ParserException extends RuntimeException {

    public ParserException(String message) {
        super(message);
    }
}
