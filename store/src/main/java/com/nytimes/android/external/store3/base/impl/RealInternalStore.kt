package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.cache3.Cache
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.InternalStore
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.util.KeyParser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map
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
internal class RealInternalStore<Raw, Parsed, Key>(
  private val fetcher: Fetcher<Raw, Key>,
  private val persister: Persister<Raw, Key>,
  private val parser: KeyParser<Key, Raw, Parsed>,
  memoryPolicy: MemoryPolicy?,
  private val stalePolicy: StalePolicy
) :
    Fetcher<Raw, Key> by fetcher,
    Persister<Raw, Key> by persister,
    KeyParser<Key, Raw, Parsed> by parser,
    InternalStore<Parsed, Key> {
  private val inFlightRequests: Cache<Key, Deferred<Parsed>> = CacheFactory.createInflighter(memoryPolicy)
  var memCache: Cache<Key, Deferred<Parsed>> = CacheFactory.createCache(memoryPolicy)
  private val inFlightScope = CoroutineScope(SupervisorJob())
  private val memoryScope = CoroutineScope(SupervisorJob())
//  private val refreshSubject = PublishSubject.create<Key>()
  private val subject = BroadcastChannel<Pair<Key, Parsed>?>(CONFLATED).apply {
    //a conflated channel always maintains the last element, the stream method ignore this element.
    //Here we add an empty element that will be ignored later
    offer(null)
  }

  constructor(
    fetcher: Fetcher<Raw, Key>,
    persister: Persister<Raw, Key>,
    parser: KeyParser<Key, Raw, Parsed>,
    stalePolicy: StalePolicy
  ) : this(fetcher, persister, parser, null, stalePolicy)

  /**
   * @param key
   * @return an observable from the first data source that is available
   */
  override suspend fun get(key: Key): Parsed =
    withContext(Dispatchers.IO) {
      try {
          memCache.get(key) {
            memoryScope.async {
              disk(key) ?: fresh(key)
            }
          }
                  .await()
      } catch (e: Exception) {
        memCache.invalidate(key)
        throw e
      }
    }



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

  suspend fun readDisk(key: Key): Parsed? {
    return try {
      val diskValue: Parsed? = read(key)
          ?.let { apply(key, it) }
      if (stalePolicy == StalePolicy.REFRESH_ON_STALE && StoreUtil.persisterIsStale<Any, Key>(key, persister)) {
        backfillCache(key)
      }
      diskValue
    } catch (e: Exception) {
      //store fetching acts as a fallthrough,
      // if we error on disk fetching we should return no data rather than throwing the error
      null
    }
  }

  private fun updateMemory(
    key: Key,
    it: Parsed
  ) {
    memCache.put(key, memoryScope.async { it })
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
  override suspend fun fresh(key: Key): Parsed =
    withContext(Dispatchers.IO) {
        fetchAndPersist(key).also {
            updateMemory(key, it)
        }
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
      val diskValue = readDisk(key)!!
      notifySubscribers(diskValue, key)
      return diskValue
    } catch (e: Exception) {
      handleNetworkError(key, e)
    } finally {
      inFlightRequests.invalidate(key)
    }
  }

  suspend fun handleNetworkError(
    key: Key,
    throwable: Throwable
  ): Parsed {
    if (stalePolicy == StalePolicy.NETWORK_BEFORE_STALE) {
      val diskValue = readDisk(key)
      if (diskValue != null)
        return diskValue else throw throwable
    }
    throw throwable
  }

  suspend fun notifySubscribers(
    data: Parsed,
    key: Key
  ) {
    subject.send(key to data)
  }

  //STREAM NO longer calls get
  override fun stream(key: Key): ReceiveChannel<Parsed> =
    streamSubscription().filter { it.first == key }.map { (_, value) -> value }

  override fun stream(): ReceiveChannel<Parsed> {
    return streamSubscription().map { (_, value) -> value }
  }

  private fun streamSubscription() =
          subject.openSubscription()
                  //ignore first element so only new elements are returned
                  .apply { poll() }
                  .map { it!! }

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
//    refreshSubject.onNext(key)
  }
}

