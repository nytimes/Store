package com.nytimes.android.sample.activity;

import android.support.v7.app.AppCompatActivity;


public class StoreActivity extends AppCompatActivity {

//    private RecyclerView recyclerView;
//    private PostAdapter postAdapter;
//    private Store<RedditData, BarCode> nonPersistedStore;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_store);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
//        postAdapter = new PostAdapter();
//        recyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(postAdapter);
//    }
//
//    private void initStore() {
//        if (this.nonPersistedStore == null) {
//            this.nonPersistedStore = ((SampleApp) getApplicationContext()).getNonPersistedStore();
//        }
//    }
//
//    @SuppressWarnings("CheckReturnValue")
//    public void loadPosts() {
//        BarCode awwRequest = new BarCode(RedditData.class.getSimpleName(), "aww");
//
//        /*
//        First call to get(awwRequest) will use the network, then save response in the in-memory
//        cache. Subsequent calls will retrieve the cached version of the data.
//
//        But, since the policy of this store is to expire after 10 seconds, the cache will
//        only be used for subsequent requests that happen within 10 seconds of the initial request.
//        After that, the request will use the network.
//         */
//        this.nonPersistedStore
//                .get(awwRequest)
//                .flatMapObservable(new Function<RedditData, ObservableSource<Post>>() {
//                    @Override
//                    public ObservableSource<Post> apply(@NonNull RedditData redditData) throws Exception {
//                        return sanitizeData(redditData);
//                    }
//                })
//                .toList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::showPosts, throwable -> {
//                    Log.e(StoreActivity.class.getSimpleName(), throwable.getMessage(), throwable);
//                });
//    }
//
//    private void showPosts(List<Post> posts) {
//        postAdapter.setPosts(posts);
//        makeText(StoreActivity.this,
//                "Loaded " + posts.size() + " posts",
//                Toast.LENGTH_SHORT)
//                .show();
//    }
//
//    private Observable<Post> sanitizeData(RedditData redditData) {
//        return Observable.fromIterable(redditData.data().children())
//                .map(Children::data);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        initStore();
//        loadPosts();
//    }
}
