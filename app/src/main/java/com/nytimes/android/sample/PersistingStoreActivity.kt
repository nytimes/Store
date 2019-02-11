package com.nytimes.android.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.sample.data.model.Children
import com.nytimes.android.sample.data.model.Post
import com.nytimes.android.sample.data.model.RedditData
import com.nytimes.android.sample.reddit.PostAdapter
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_store.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class PersistingStoreActivity : AppCompatActivity(), CoroutineScope {
    lateinit var job: Job

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Main

    lateinit var postAdapter: PostAdapter
    lateinit var persistedStore: Store<RedditData, BarCode>
    lateinit var moshi: Moshi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

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
        launch {
            showPosts(
                    sanitizeData(
                            persistedStore.get(awwRequest)))
        }

    }

    private fun showPosts(posts: List<Post>) {
        postAdapter.setPosts(posts)
        makeText(this@PersistingStoreActivity,
                "Loaded ${posts.size} posts",
                Toast.LENGTH_SHORT)
                .show()
    }

    fun sanitizeData(redditData: RedditData): List<Post> =
            redditData.data.children.map(Children::data)

    override fun onResume() {
        super.onResume()
        loadPosts()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // all children coroutines gets destroyed automatically
    }
}
