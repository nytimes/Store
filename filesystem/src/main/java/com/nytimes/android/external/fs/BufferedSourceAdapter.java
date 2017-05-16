package com.nytimes.android.external.fs;

import javax.annotation.Nonnull;

import okio.BufferedSource;

/**
 * Converts Java values to JSON.
 */
public interface BufferedSourceAdapter<Parsed> {
    @Nonnull
    BufferedSource toJson(@Nonnull Parsed value);
}
