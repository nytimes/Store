package com.nytimes.android.sample;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nytimes.android.external.fs3.SourcePersisterFactory;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.MemoryPolicy;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import com.nytimes.android.sample.data.model.GsonAdaptersModel;
import com.nytimes.android.sample.data.model.RedditData;
import com.nytimes.android.sample.data.remote.Api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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

    /**
     * Provides a Store which only retains RedditData for 10 seconds in memory.
     */
    private Store<RedditData, BarCode> provideRedditStore() {
        return StoreBuilder.<RedditData>barcode()
                .fetcher(barCode -> provideRetrofit().fetchSubreddit(barCode.getKey(), "10"))
                .memoryPolicy(
                        MemoryPolicy
                                .builder()
                                .setExpireAfterWrite(10)
                                .setExpireAfterTimeUnit(TimeUnit.SECONDS)
                                .build()
                )
                .open();
    }

    /**
     * Provides a Store which will persist RedditData to the cache, and use Gson to parse the JSON
     * that comes back from the network into RedditData.
     */
    private Store<RedditData, BarCode> providePersistedRedditStore() {
        return StoreBuilder.<BarCode, BufferedSource, RedditData>parsedWithKey()
                .fetcher(this::fetcher)
                .persister(persister)
                .parser(GsonParserFactory.createSourceParser(provideGson(), RedditData.class))
                .open();
    }

    /**
     * Returns a new Persister with the cache as the root.
     */
    private Persister<BufferedSource, BarCode> newPersister() throws IOException {
        return SourcePersisterFactory.create(getApplicationContext().getCacheDir());
    }

    /**
     * Returns a "fetcher" which will retrieve new data from the network.
     */
    @NonNull
    private Single<BufferedSource> fetcher(BarCode barCode) {
        return provideRetrofit().fetchSubredditForPersister(barCode.getKey(), "10")
                .map(ResponseBody::source);
    }

    private Api provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://reddit.com/")
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
