package com.nytimes.android.sample.data.model;

import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Post {
    @Nullable
    public abstract Preview preview();

    public abstract String title();

    public abstract int score();

    public abstract String author();

    @SerializedName("created_utc")
    public abstract long created();

    public abstract String url();

    @Nullable
    public abstract Integer height();

    @Nullable
    public abstract Integer width();

    @Value.Derived
    public Optional<Image> nestedThumbnail() {
        if (preview() == null || preview().images() == null || preview().images().get(0).source() == null)
            return Optional.absent();
        return Optional.of(preview().images().get(0).source());
    }

}
