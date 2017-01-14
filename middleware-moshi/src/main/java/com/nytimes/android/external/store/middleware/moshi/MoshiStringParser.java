package com.nytimes.android.external.store.middleware.moshi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

public class MoshiStringParser<Parsed> implements Parser<String, Parsed> {

    private final JsonAdapter<Parsed> jsonAdapter;

    @Inject
    public MoshiStringParser(@NonNull Moshi moshi, @NonNull Type type) {
        jsonAdapter = moshi.adapter(type);
    }

    @Override
    @Nullable
    public Parsed call(@NonNull String source) {
        try {
            return jsonAdapter.fromJson(source);
        } catch (IOException e) {
            return null;
        }
    }
}
