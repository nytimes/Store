package com.nytimes.android.external.store.middleware.moshi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import javax.inject.Inject;

public class MoshiReaderParser<Parsed> implements Parser<Reader, Parsed> {

    private final JsonAdapter<Parsed> jsonAdapter;

    @Inject
    public MoshiReaderParser(@NonNull Moshi moshi, @NonNull Type type) {
        jsonAdapter = moshi.adapter(type);
    }

    @Override
    @Nullable
    public Parsed call(@NonNull Reader source) {
        String jsonString;
        try {
            jsonString = IOUtils.toString(source);
            return jsonAdapter.fromJson(jsonString);
        } catch (IOException e) {
            return null;
        }
    }

}
