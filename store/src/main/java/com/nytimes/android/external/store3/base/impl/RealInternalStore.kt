package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.cache3.Cache
import com.nytimes.android.external.store.util.Result
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.InternalStore
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.util.KeyParser
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import java.util.*
import java.util.concurrent.ConcurrentMap

/**
 * Store to be used for loading an object from different data sources
 *
 * @param <Raw>    data type before parsing, usually a String, Reader or BufferedSource
 * @param <Parsed> data type after parsing
 *
 *
 * Example usage:  @link
</Parsed></Raw> */
internal class RealInternalStore<Raw, Parsed, Key>(private val fetcher: Fetcher<Raw, Key>,
                                                   private val persister: Persister<Raw, Key>,
                                                   private val parser: KeyParser<Key, Raw, Parsed>,
                                                   memoryPolicy: MemoryPolicy?,
                                                   private val stalePolicy: StalePolicy) :
        Fetcher<Raw, Key> by fetcher,
        Persister<Raw, Key> by persister,
        KeyParser<Key, Raw, Parsed> by parser,
        InternalStore<Parsed, Key> {
    val inFlightRequests: Cache<Key, Deferred<Parsed>>
    var memCache: Cache<Key, Deferred<Parsed>>
    private val inFlightScope = CoroutineScope(SupervisorJob())
    private val memoryScope = CoroutineScope(SupervisorJob())
    private val refreshSubject = PublishSubject.create<Key>()
    private val subject: PublishSubject<AbstractMap.SimpleEntry<Key, Parsed>>

    constructor(fetcher: Fetcher<Raw, Key>,
                persister: Persister<Raw, Key>,
                parser: KeyParser<Key, Raw, Parsed>,
                stalePolicy: StalePolicy) : this(fetcher, persister, parser, null, stalePolicy) {
    }

    init {
        this.memCache = CacheFactory.createCache(memoryPolicy)
        this.inFlightRequests = CacheFactory.createInflighter(memoryPolicy)
        subject = PublishSubject.create<AbstractMap.SimpleEntry<Key, Parsed>>()
    }

    /**
     * @param key
     * @return an observable from the first data source that is available
     */
    override suspend fun get(key: Key): Parsed {
        return memCache.get(key) {
            memoryScope.async {
                return@async disk(key)?:fresh(key)
            }
        }.await()
    }


//    @Experimental
//    override fun getRefreshing(key: Key): Observable<Parsed> {
//        return get(key)
//                .toObservable()
//                .compose(StoreUtil.repeatWhenSubjectEmits(refreshSubject, key))
//    }


//    /**
//     * @return data from memory
//     */
//    private fun lazyCache(key: Key): Maybe<Parsed> {
//        return Maybe
//                .defer { cache(key) }
//                .onErrorResumeNext(Maybe.empty())
//    }

//    fun cache(key: Key): Parsed? {
//        try {
//            return memCache.get(key) { memoryScope.async { disk(key) } }.await()
//        } catch (e: ExecutionException) {
//            return Maybe.empty()
//        }
//
//    }

//    /**
//     * @return data from memory
//     */
//    private fun lazyCacheWithResult(key: Key): Maybe<Result<Parsed>> {
//        return Maybe
//                .defer { cacheWithResult(key) }
//                .onErrorResumeNext(Maybe.empty())
//    }
//
//    fun cacheWithResult(key: Key): Maybe<Result<Parsed>> {
//        try {
//            val maybeResult = memCache.get(key) { disk(key) }
//            return if (maybeResult == null) Maybe.empty() else maybeResult.map { Result.createFromCache(it) }
//        } catch (e: ExecutionException) {
//            return Maybe.empty()
//        }
//
//    }


//    suspend override fun memory(key: Key): Parsed? {
//        val cachedValue = memCache.getIfPresent(key)
//        return cachedValue
//    }

    /**
     * Fetch data from persister and update memory after. If an error occurs, emit an empty observable
     * so that the concat call in [.get] moves on to [.fresh]
     *
     * @param key
     * @return
     */
    override suspend fun disk(key: Key): Parsed? {
        return if (StoreUtil.shouldReturnNetworkBeforeStale<Raw, Key>(persister, stalePolicy, key)) {
            null
        } else readDisk(key)

    }

//    fun readDisk(key: Key): Maybe<Parsed> {
//        return read(key)
//                .onErrorResumeNext(Maybe.empty())
//                .map { raw -> apply(key, raw) }
//                .doOnSuccess { parsed ->
//                    updateMemory(key, parsed)
//                    if (stalePolicy == StalePolicy.REFRESH_ON_STALE && StoreUtil.persisterIsStale<Any, Key>(key, persister)) {
//                        backfillCache(key)
//                    }
//                }.cache()
//    }


    suspend fun readDisk(key: Key): Parsed? {
        return try {
            val diskValue: Parsed? = read(key)
                    ?.let { apply(key, it) }
//                    ?.also { updateMemory(key, it) } //TODO MIKE: check whether we need to update the cache on the way back
            if (stalePolicy == StalePolicy.REFRESH_ON_STALE) {
                backfillCache(key)
            }
            diskValue;
        } catch (e: Exception) {
            //store fetching acts as a fallthrough,
            // if we error on disk fetching we should return no data rather than throwing the error
            null
        }
    }

    suspend fun backfillCache(key: Key) {
        fresh(key)
    }


    /**
     * Will check to see if there exists an in flight observable and return it before
     * going to network
     *
     * @return data from fresh and store it in memory and persister
     */
    override suspend fun fresh(key: Key): Parsed {
        return fetchAndPersist(key)
    }

    override suspend fun freshWithResult(key: Key): Result<Parsed> {
        return fresh(key).let { Result.createFromNetwork(it) }
    }

    /**
     * There should only be one fresh request in flight at any give time.
     *
     *
     * Return cached request in the form of a Behavior Subject which will emit to its subscribers
     * the last value it gets. Subject/Observable is cached in a [ConcurrentMap] to maintain
     * thread safety.
     *
     * @param key resource identifier
     * @return observable that emits a [Parsed] value
     */
    suspend fun fetchAndPersist(key: Key): Parsed =
            inFlightRequests
                    .get(key) { inFlightScope.async { response(key) } }
                    .await()


    suspend fun response(key: Key): Parsed {
        return try {
            val fetchedValue = fetch(key)

            write(key, fetchedValue)
            return readDisk(key)!!
        } catch (e: Exception) {
            handleNetworkError(key, e)
        }


    }

    suspend fun handleNetworkError(key: Key, throwable: Throwable): Parsed {
        if (stalePolicy == StalePolicy.NETWORK_BEFORE_STALE) {
            val diskValue = readDisk(key)
            if (diskValue != null)
                return diskValue else throw throwable
        }
        throw throwable
    }

    fun notifySubscribers(data: Parsed, key: Key) {
        subject.onNext(AbstractMap.SimpleEntry(key, data))
    }

    /**
     * Get data stream for Subjects with the argument id
     *
     * @return
     */
    override fun stream(key: Key): Observable<Parsed> {
        TODO("not yet implemented")
//        return subject
//                .hide()
//                .startWith(get(key).toObservable().map<AbstractMap.SimpleEntry<Key, Parsed>> { data -> AbstractMap.SimpleEntry(key, data) })
//                .filter { simpleEntry -> simpleEntry.key == key }
//                .map({ it.value })
    }

    override fun stream(): Observable<Parsed> {
        return subject.hide().map({ it.value })
    }


//    /**
//     * Only update memory after persister has been successfully updated
//     *
//     * @param key
//     * @param data
//     */
//    fun updateMemory(key: Key, data: Parsed) {
//        memCache.put(key, Maybe.just(data))
//    }

    @Deprecated("")
    override fun clearMemory() {
        clear()
    }

    /**
     * Clear memory by id
     *
     * @param key of data to clear
     */
    @Deprecated("")
    override fun clearMemory(key: Key) {
        clear(key)
    }


    override fun clear() {
        for (cachedKey in memCache.asMap().keys) {
            clear(cachedKey)
        }
    }

    override fun clear(key: Key) {
        inFlightRequests.invalidate(key)
        memCache.invalidate(key)
        StoreUtil.clearPersister<Any, Key>(persister, key)
        notifyRefresh(key)
    }

    private fun notifyRefresh(key: Key) {
        refreshSubject.onNext(key)
    }

}

