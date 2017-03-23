package com.nytimes.android.external.fs2;


import javax.annotation.Nonnull;

/**
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 *
 * @param <T> Store key/request param type
 */
public interface PathResolver<T> {

    @Nonnull
    String resolve(@Nonnull T key);
}
