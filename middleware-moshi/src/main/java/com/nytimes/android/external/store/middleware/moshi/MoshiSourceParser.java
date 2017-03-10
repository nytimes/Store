package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import okio.BufferedSource;

public class MoshiSourceParser<Parsed> implements Parser<BufferedSource, Parsed> {

    private final JsonAdapter<Parsed> jsonAdapter;

    @Inject
    public MoshiSourceParser(@Nonnull Moshi moshi, @Nonnull Type type) {
        jsonAdapter = moshi.adapter(type);
    }

    @Override
    @SuppressWarnings({"PMD.EmptyCatchBlock", "PMD.SignatureDeclareThrowsException"})
    public Parsed apply(@NonNull BufferedSource bufferedSource) throws Exception {
        try {
            return jsonAdapter.fromJson(bufferedSource);
        } catch (IOException e) {
            return null;
        }
    }
}
