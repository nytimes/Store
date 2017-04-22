package com.nytimes.android.sample.data.remote;

import com.nytimes.android.sample.data.model.RedditData;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("r/{subredditName}/new/.json")
    Single<RedditData> fetchSubreddit(@Path("subredditName") String subredditName,
                                      @Query("limit") String limit);

    @GET("r/{subredditName}/new/.json")
    Single<ResponseBody> fetchSubredditForPersister(@Path("subredditName") String subredditName,
                                                    @Query("limit") String limit);
}
