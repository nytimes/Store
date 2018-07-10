package com.nytimes.android.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.sample.R.id.postRecyclerView
import com.nytimes.android.sample.data.model.Post
import com.nytimes.android.sample.data.model.RedditData
import com.nytimes.android.sample.reddit.PostAdapter
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_store.*


class PersistingStoreActivity : AppCompatActivity() {

    lateinit var postAdapter: PostAdapter
    lateinit var persistedStore: Store<RedditData, BarCode>
    lateinit var moshi: Moshi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_store)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)

        postAdapter = PostAdapter()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        postRecyclerView.layoutManager = layoutManager
        postRecyclerView.adapter = postAdapter
        persistedStore = (applicationContext as SampleApp).persistedStore
        moshi = (applicationContext as SampleApp).moshi
    }

    fun loadPosts() {
        val awwRequest = BarCode(RedditData::class.java.simpleName, "aww")

        /*
        First call to get(awwRequest) will use the network, then save response in the in-memory
        cache. Subsequent calls will retrieve the cached version of the data.
         */
        this.persistedStore
                .get(awwRequest)
                .flatMapObservable { sanitizeData(it) }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ showPosts(it) }, {it -> Log.e(PersistingStoreActivity::class.java.simpleName, it.message, it)})
    }

    private fun showPosts(posts: List<Post>) {
        postAdapter.setPosts(posts)
        makeText(this@PersistingStoreActivity,
                "Loaded ${posts.size} posts",
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun sanitizeData(redditData: RedditData): Observable<Post> {
        return Observable.fromIterable(redditData.data.children)
                .map({ it.data })
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
    }
}
