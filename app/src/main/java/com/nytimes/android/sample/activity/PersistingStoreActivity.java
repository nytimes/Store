package com.nytimes.android.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nytimes.android.external.fs.SourcePersister;
import com.nytimes.android.external.fs.impl.FileSystemImpl;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.ParsingStoreBuilder;
import com.nytimes.android.external.store.middleware.GsonSourceParser;
import com.nytimes.android.sample.BuildConfig;
import com.nytimes.android.sample.data.model.Children;
import com.nytimes.android.sample.data.model.GsonAdaptersModel;
import com.nytimes.android.sample.data.model.Post;
import com.nytimes.android.sample.data.model.RedditData;
import com.nytimes.android.sample.data.remote.Api;

import java.io.IOException;
import java.util.List;

import okio.BufferedSource;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.widget.Toast.makeText;


public class PersistingStoreActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            loadPosts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadPosts() throws IOException {
        BarCode awwRequest = new BarCode(RedditData.class.getSimpleName(), "aww");
        provideRedditStore()
                .get(awwRequest)
                .flatMap(this::sanitizeData)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showPosts, throwable -> {});
    }

    private void showPosts(List<Post> posts) {
        makeText(PersistingStoreActivity.this,
                "Loaded " + posts.size() + " posts",
                Toast.LENGTH_SHORT)
                .show();
    }

    private Observable<Post> sanitizeData(RedditData redditData) {
        return Observable.from(redditData.data().children())
                .map(Children::data);
    }

    private Store<RedditData> provideRedditStore() throws IOException {
        return ParsingStoreBuilder.<BufferedSource,RedditData>builder()
                .fetcher(this::fetcher)
                .persister(persister())
                .parser(new GsonSourceParser<>(provideGson(),RedditData.class))
                .open();
    }

    private SourcePersister persister() throws IOException {
        return new SourcePersister(new FileSystemImpl(getApplicationContext().getCacheDir()));
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
