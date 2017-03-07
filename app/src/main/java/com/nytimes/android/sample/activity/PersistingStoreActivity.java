package com.nytimes.android.sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.nytimes.android.external.store.base.impl.BarCode;
import com.nytimes.android.external.store.base.impl.Store;
import com.nytimes.android.sample.R;
import com.nytimes.android.sample.SampleApp;
import com.nytimes.android.sample.data.model.Children;
import com.nytimes.android.sample.data.model.Post;
import com.nytimes.android.sample.data.model.RedditData;
import com.nytimes.android.sample.reddit.PostAdapter;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.widget.Toast.makeText;


public class PersistingStoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private Store<RedditData, BarCode> persistedStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        postAdapter = new PostAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);
    }

    private void initStore() {
        if (this.persistedStore == null) {
            this.persistedStore = ((SampleApp) getApplicationContext()).getPersistedStore();
        }
    }

    public void loadPosts() {
        BarCode awwRequest = new BarCode(RedditData.class.getSimpleName(), "aww");

        this.persistedStore
                .get(awwRequest)
                .flatMap(this::sanitizeData)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showPosts, throwable -> {
                });
    }

    private void showPosts(List<Post> posts) {
        postAdapter.setPosts(posts);
        makeText(PersistingStoreActivity.this,
                "Loaded " + posts.size() + " posts",
                Toast.LENGTH_SHORT)
                .show();
    }

    private Observable<Post> sanitizeData(RedditData redditData) {
        return Observable.from(redditData.data().children())
                .map(Children::data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStore();
        loadPosts();
    }
}
