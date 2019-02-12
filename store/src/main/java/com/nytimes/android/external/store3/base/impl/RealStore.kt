package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store.util.Result
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.InternalStore
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.util.KeyParser
import com.nytimes.android.external.store3.util.NoKeyParser
import com.nytimes.android.external.store3.util.NoopParserFunc
import com.nytimes.android.external.store3.util.NoopPersister
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.channels.ReceiveChannel

open class RealStore<Parsed, Key> : Store<Parsed, Key> {

    private val internalStore: InternalStore<Parsed, Key>

    constructor(internalStore: InternalStore<Parsed, Key>) {
        this.internalStore = internalStore
    }

    constructor(fetcher: Fetcher<Parsed, Key>) {
        val noOpFunc = NoopParserFunc<Parsed, Parsed>()
        internalStore = RealInternalStore(fetcher, NoopPersister.create(),
                NoKeyParser(noOpFunc), StalePolicy.UNSPECIFIED)
    }

    constructor(fetcher: Fetcher<Parsed, Key>,
                persister: Persister<Parsed, Key>) {
        val noOpFunc = NoopParserFunc<Parsed, Parsed>()
        internalStore = RealInternalStore(fetcher,
                persister,
                NoKeyParser(noOpFunc),
                StalePolicy.UNSPECIFIED)
    }

    constructor(fetcher: Fetcher<*, Key>,
                persister: Persister<*, Key>,
                parser: Parser<*, Parsed>) {
        internalStore = RealInternalStore(fetcher as Fetcher<Any, Key>,
                persister as Persister<Any, Key>,
                NoKeyParser(parser as Parser<Any, Parsed>),
                StalePolicy.UNSPECIFIED)
    }


    constructor(fetcher: Fetcher<Any, Key>,
                persister: Persister<Any, Key>,
                parser: Parser<Any, Parsed>,
                memoryPolicy: MemoryPolicy,
                policy: StalePolicy) {
        internalStore = RealInternalStore<Any, Parsed, Key>(fetcher, persister,
                NoKeyParser<Key, Any, Parsed>(parser), memoryPolicy, policy)
    }

    constructor(fetcher: Fetcher<Any, Key>,
                persister: Persister<Any, Key>,
                parser: KeyParser<Key, Any, Parsed>,
                memoryPolicy: MemoryPolicy,
                policy: StalePolicy) {
        internalStore = RealInternalStore<Any, Parsed, Key>(fetcher, persister,
                parser, memoryPolicy, policy)
    }


    suspend override fun get(key: Key): Parsed {
        return internalStore.get(key)
    }

    fun getWithResult(key: Key): Single<Result<Parsed>> {
        TODO("not implemented")
    }

    fun getRefreshing(key: Key): Observable<Parsed> {
        TODO("not implemented")
    }


    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to network
     *
     * @return data from fresh and store it in memory and persister
     */
    suspend override fun fresh(key: Key): Parsed {
        return internalStore.fresh(key)
    }



    override fun stream(): ReceiveChannel<Parsed> {
        return internalStore.stream()
    }

    override fun stream(key: Key): ReceiveChannel<Parsed> {
        return internalStore.stream(key)
    }

    override fun clearMemory() {
        internalStore.clearMemory()
    }

    /**
     * Clear memory by id
     *
     * @param key of data to clear
     */
    override fun clearMemory(key: Key) {
        internalStore.clearMemory(key)
    }

    override fun clear() {
        internalStore.clear()
    }

    override fun clear(key: Key) {
        internalStore.clear(key)
    }

//    protected fun memory(key: Key): Maybe<Parsed> {
//        return internalStore.memory(key)
//    }

    protected suspend fun disk(key: Key): Parsed? {
        return internalStore.disk(key)
    }

}
