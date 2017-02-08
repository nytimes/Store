package com.nytimes.android.external.fs;

/**
 * Created by 206847 on 2/8/17.
 */

/**
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 * @param <T> Store key/request param type
 */
public interface PathResolver<T> {

    String resolve(T key);
}
