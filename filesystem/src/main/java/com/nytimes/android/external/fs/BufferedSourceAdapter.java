package com.nytimes.android.external.fs;

import javax.annotation.Nonnull;

import okio.BufferedSource;

/**
 * Converts Java values to JSON, and JSON values to Java.
 */
public interface BufferedSourceAdapter<Parsed> {
    @Nonnull
    BufferedSource toJson(@Nonnull Parsed value);

    @Nonnull
    Parsed fromJson(@Nonnull BufferedSource source);
}
