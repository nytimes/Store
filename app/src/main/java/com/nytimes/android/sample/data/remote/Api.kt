package com.nytimes.android.sample.data.remote

import com.nytimes.android.sample.data.model.RedditData

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("r/{subredditName}/new/.json")
    fun fetchSubreddit(@Path("subredditName") subredditName: String,
                       @Query("limit") limit: String): Single<RedditData>

    @GET("r/{subredditName}/new/.json")
    fun fetchSubredditForPersister(@Path("subredditName") subredditName: String,
                                   @Query("limit") limit: String): Single<ResponseBody>
}
