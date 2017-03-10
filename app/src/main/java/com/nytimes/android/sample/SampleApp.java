package com.nytimes.android.sample;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nytimes.android.external.fs.SourcePersisterFactory;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.MemoryPolicy;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.external.store.base.impl.StoreBuilder;
import com.nytimes.android.external.store.middleware.GsonParserFactory;
import com.nytimes.android.sample.data.model.GsonAdaptersModel;
import com.nytimes.android.sample.data.model.RedditData;
import com.nytimes.android.sample.data.remote.Api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;

public class SampleApp extends Application {

    private Store<RedditData, BarCode> nonPersistedStore;
    private Store<RedditData, BarCode> persistedStore;
    private Persister<BufferedSource, BarCode> persister;

    @Override
    public void onCreate() {
        super.onCreate();

        initPersister();
        this.nonPersistedStore = provideRedditStore();
        this.persistedStore = providePersistedRedditStore();
    }

    private void initPersister() {
        try {
            persister = newPersister();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Store<RedditData, BarCode> getNonPersistedStore() {
        return this.nonPersistedStore;
    }

    public Store<RedditData, BarCode> getPersistedStore() {
        return this.persistedStore;
    }

    private Store<RedditData, BarCode> provideRedditStore() {
        return StoreBuilder.<RedditData>barcode()
                .fetcher(barCode -> provideRetrofit().fetchSubreddit(barCode.getKey(), "10"))
                .memoryPolicy(
                    MemoryPolicy
                        .builder()
                        .setExpireAfter(10)
                        .setExpireAfterTimeUnit(TimeUnit.SECONDS)
                        .build()
                )
                .open();
    }

    private Store<RedditData, BarCode> providePersistedRedditStore() {
        return StoreBuilder.<BarCode, BufferedSource, RedditData>parsedWithKey()
                .fetcher(this::fetcher)
                .persister(persister)
                .parser(GsonParserFactory.createSourceParser(provideGson(), RedditData.class))
                .open();
    }

    private Persister<BufferedSource, BarCode> newPersister() throws IOException {
        return SourcePersisterFactory.create(getApplicationContext().getCacheDir());
    }

    private Observable<BufferedSource> fetcher(BarCode barCode) {
        return provideRetrofit().fetchSubredditForPersister(barCode.getKey(), "10")
                .map(responseBody -> responseBody.source());
    }

    private Api provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://reddit.com/")
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .validateEagerly(BuildConfig.DEBUG)  // Fail early: check Retrofit configuration at creation time in Debug build.
                .build()
                .create(Api.class);
    }

    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new GsonAdaptersModel())
                .create();
    }
}
