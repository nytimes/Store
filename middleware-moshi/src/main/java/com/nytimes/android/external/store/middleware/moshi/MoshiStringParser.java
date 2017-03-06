package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoshiStringParser<Parsed> implements Parser<String, Parsed> {

    private final JsonAdapter<Parsed> jsonAdapter;

    public MoshiStringParser(@Nonnull Moshi moshi, @Nonnull Type type) {
        jsonAdapter = moshi.adapter(type);
    }

    @Override
    @Nullable
    public Parsed call(@Nonnull String source) {
        try {
            return jsonAdapter.fromJson(source);
        } catch (IOException e) {
            return null;
        }
    }
}
