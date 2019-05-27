package com.nytimes.android.external.fs3

/**
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 *
 * @param <T> Store key/request param type
 */
interface PathResolver<T> {

    fun resolve(key: T): String
}
