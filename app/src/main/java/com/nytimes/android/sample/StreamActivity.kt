package com.nytimes.android.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class StreamActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stream_activity)

        var counter = 0

        val store = StoreBuilder.key<Int, Int>()
                .fetcher { key -> key * 1000 + counter++ }
                .memoryPolicy(
                        MemoryPolicy
                                .builder()
                                .setExpireAfterWrite(10)
                                .setExpireAfterTimeUnit(TimeUnit.SECONDS)
                                .build()
                )
                .open()

        findViewById<View>(R.id.get_1).onClick {
            store.get(1)
        }

        findViewById<View>(R.id.fresh_1).onClick {
            store.fresh(1)
        }

        findViewById<View>(R.id.get_2).onClick {
            store.get(2)
        }

        findViewById<View>(R.id.fresh_2).onClick {
            store.fresh(2)
        }

        launch {
            store.stream(1).collect {
                findViewById<TextView>(R.id.stream_1).text = "Stream 1 $it"
            }
        }
        launch {
            store.stream(2).collect {
                findViewById<TextView>(R.id.stream_2).text = "Stream 2 $it"
            }
        }
        launch {
            store.stream().collect {
                findViewById<TextView>(R.id.stream).text = "Stream $it"
            }
        }
    }

    fun View.onClick(f: suspend () -> Unit) {
        setOnClickListener {
            launch {
                f()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}