package com.nytimes.android.sample

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nytimes.android.external.fs3.SourcePersisterFactory
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.middleware.GsonParserFactory
import com.nytimes.android.sample.data.model.GsonAdaptersModel
import com.nytimes.android.sample.data.model.RedditData
import com.nytimes.android.sample.data.remote.Api
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okio.BufferedSource
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class SampleApp : Application() {

    var nonPersistedStore: Store<RedditData, BarCode>? = null
    var  persistedStore: Store<RedditData, BarCode>? =null
    private var persister: Persister<BufferedSource, BarCode>? =null
    private val sampleRoomStore=SampleRoomStore(this)

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initPersister();
        nonPersistedStore = provideRedditStore();
        persistedStore=providePersistedRedditStore();
        RoomSample()
    }

    private fun RoomSample() {
        var foo = sampleRoomStore.store.get("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ strings1 -> val success = strings1 != null }) { throwable -> throwable.stackTrace }

        foo = Observable.timer(15, TimeUnit.SECONDS)
                .subscribe { makeFetchRequest() }
    }

    private fun makeFetchRequest() {
        val bar = sampleRoomStore.store.fetch("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ strings1 -> val success = strings1 != null }) { throwable -> throwable.stackTrace }
    }

    private fun initPersister() {
        try {
            persister = newPersister()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }

    }

    /**
     * Provides a Store which only retains RedditData for 10 seconds in memory.
     */
    private fun provideRedditStore(): Store<RedditData, BarCode> {
        return StoreBuilder.barcode<RedditData>()
                .fetcher { barCode -> provideRetrofit().fetchSubreddit(barCode.key, "10") }
                .memoryPolicy(
                        MemoryPolicy
                                .builder()
                                .setExpireAfterWrite(10)
                                .setExpireAfterTimeUnit(TimeUnit.SECONDS)
                                .build()
                )
                .open()
    }

    /**
     * Provides a Store which will persist RedditData to the cache, and use Gson to parse the JSON
     * that comes back from the network into RedditData.
     */
    private fun providePersistedRedditStore(): Store<RedditData, BarCode> {
        return StoreBuilder.parsedWithKey<BarCode, BufferedSource, RedditData>()
                .fetcher({ this.fetcher(it) })
                .persister(newPersister())
                .parser(GsonParserFactory.createSourceParser(provideGson(), RedditData::class.java))
                .open()
    }

    /**
     * Returns a new Persister with the cache as the root.
     */
    @Throws(IOException::class)
    private fun newPersister(): Persister<BufferedSource, BarCode> {
        return SourcePersisterFactory.create(this.cacheDir)
    }

    /**
     * Returns a "fetcher" which will retrieve new data from the network.
     */
    private fun fetcher(barCode: BarCode): Single<BufferedSource> {
        return provideRetrofit().fetchSubredditForPersister(barCode.key, "10")
                .map({ it.source() })
    }

    private fun provideRetrofit(): Api {
        return Retrofit.Builder()
                .baseUrl("http://reddit.com/")
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .validateEagerly(BuildConfig.DEBUG)  // Fail early: check Retrofit configuration at creation time in Debug build.
                .build()
                .create(Api::class.java)
    }

    internal fun provideGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapterFactory(GsonAdaptersModel())
                .create()
    }

    companion object {
        var appContext: Context? = null
    }
}
