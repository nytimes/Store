package com.nytimes.android.sample.data.remote;

import com.nytimes.android.external.store.base.BuildStore;
import com.nytimes.android.sample.data.model.RedditData;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@BuildStore
public interface Api {

    @GET("r/{subredditName}/new/.json")
    Observable<RedditData> fetchSubreddit(@Path("subredditName") String subredditName,
                                          @Query("limit") String limit);

    @GET("r/{subredditName}/new/.json")
    Observable<ResponseBody> fetchSubredditForPersister(@Path("subredditName") String subredditName,
                                                        @Query("limit") String limit);
}


/*
 @GET("r/{subredditName}/new/.json")
    @Persister(myImpl = Persister.class)
    Observable<RedditData> fetchSubreddit(@Path("subredditName") String subredditName,
                                          @Query("limit") String limit);

    @GET("r/{subredditName}/new/.json")
    @Type(RedditData.class)
    @Persister(fileLocation = "fileRoot")
    Observable<ResponseBody> fetchSubredditForPersister(@Path("subredditName") String subredditName,
                                                        @Query("limit") String limit);

    class ApiModule {
        @Provides
        @Singleton
        Store<RedditData> provideFetchSubredditStore(Api api, Persister<RedditData> persister) {
            return new RealStore<>(barCode ->
                    api.fetchSubreddit(barCode.getKey(), barCode.getType()), persister);

        }
        @Provides @Singleton
        Store<RedditData> provideFetchSubredditForPersister(Api api, String fileLocation, Gson gson) throws IOException {
            return ParsingStoreBuilder.<BufferedSource, RedditData>builder()
                    .fetcher(new Fetcher<BufferedSource>() {
                        @NonNull
                        @Override
                        public Observable<BufferedSource> fetch(APIBarCode barCode) {
                            return api.fetchSubredditForPersister(barCode.subRedditName(), barCode.getLimit())
                                    .map(ResponseBody::source);
                        }
                    })
                    .persister(SourcePersisterFactory.create(new File(fileLocation)))
                    .parser(GsonParserFactory.createSourceParser(gson,RedditData.class))
                    .open();
        }

    }

 */
