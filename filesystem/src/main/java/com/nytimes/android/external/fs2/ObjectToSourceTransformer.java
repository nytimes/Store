package com.nytimes.android.external.fs2;

import javax.annotation.Nonnull;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import okio.BufferedSource;

/**
 * This Transformer applies {@code map} to the source {@link io.reactivex.Single} in order to transform it from
 * one that emits objects of type {@code Parsed} to one that emits {@link okio.BufferedSource} of those objects.
 * <p>
 * @param <Parsed> the type of objects emitted by the source single.
 */
public class ObjectToSourceTransformer<Parsed> implements SingleTransformer<Parsed, BufferedSource> {

    @Nonnull
    protected BufferedSourceAdapter<Parsed> adapter;

    public ObjectToSourceTransformer(@Nonnull BufferedSourceAdapter<Parsed> adapter) {
        this.adapter = adapter;
    }

    @Override
    public SingleSource<BufferedSource> apply(Single<Parsed> upstream) {
        return upstream.map(object -> adapter.toJson(object));
    }
}
