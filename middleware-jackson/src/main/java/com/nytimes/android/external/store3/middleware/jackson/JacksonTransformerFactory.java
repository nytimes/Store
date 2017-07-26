package com.nytimes.android.external.store3.middleware.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.fs3.ObjectToSourceTransformer;
import com.nytimes.android.external.store3.annotations.Experimental;

import javax.annotation.Nonnull;

/**
 * Factory which returns Jackson {@link io.reactivex.SingleTransformer} implementations.
 */
public final class JacksonTransformerFactory {

    private JacksonTransformerFactory() {
    }

    /**
     * Returns a new {@link ObjectToSourceTransformer}, which uses a {@link JacksonBufferedSourceAdapter} to parse from
     * objects of the specified type to JSON using the provided
     * {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper} instance.
     */
    @Nonnull
    @Experimental
    public static <Parsed> ObjectToSourceTransformer<Parsed> createObjectToSourceTransformer(@Nonnull ObjectMapper
                                                                                                         objectMapper) {
        return new ObjectToSourceTransformer<>(new JacksonBufferedSourceAdapter<Parsed>(objectMapper));
    }

}
