package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

public class MoshiStringParser<Parsed> implements Parser<String, Parsed> {

    private final JsonAdapter<Parsed> jsonAdapter;

    @Inject
    public MoshiStringParser(@Nonnull Moshi moshi, @Nonnull Type type) {
        jsonAdapter = moshi.adapter(type);
    }


    @Override
    @SuppressWarnings({"PMD.EmptyCatchBlock"})
    public Parsed apply(@NonNull String s) {
        try {
            return jsonAdapter.fromJson(s);
        } catch (IOException e) {
            return null;
        }
    }
}
