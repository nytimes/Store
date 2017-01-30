package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

import okio.BufferedSource;

public class MoshiSourceParser<Parsed> implements Parser<BufferedSource, Parsed> {

    private final JsonAdapter<Parsed> jsonAdapter;

    @Inject
    public MoshiSourceParser(@NotNull Moshi moshi, @NotNull Type type) {
        jsonAdapter = moshi.adapter(type);
    }

    @Override
    @Nullable
    public Parsed call(BufferedSource source) {
        try {
            return jsonAdapter.fromJson(source);
        } catch (IOException e) {
            return null;
        }
    }
}
