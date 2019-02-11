package com.nytimes.android.external.store3.base.impl


import com.nytimes.android.external.store3.base.*
import com.nytimes.android.external.store3.util.KeyParser
import com.nytimes.android.external.store3.util.NoKeyParser
import com.nytimes.android.external.store3.util.NoopParserFunc
import com.nytimes.android.external.store3.util.NoopPersister


/**
 * Builder where there parser is used.
 */
class RealStoreBuilder<Raw, Parsed, Key> {
    private var parser: KeyParser<Key, Raw, Parsed>? = null
    private var persister: Persister<Raw, Key>? = null
    private var fetcher: Fetcher<Raw, Key>? = null
    private var memoryPolicy: MemoryPolicy? = null

    private//remove when it is implemented...
    var stalePolicy = StalePolicy.UNSPECIFIED

    fun fetcher(fetcher: Fetcher<Raw, Key>): RealStoreBuilder<Raw, Parsed, Key> {
        this.fetcher = fetcher
        return this
    }

    fun fetcher(fetcher: suspend (Key) -> Raw): RealStoreBuilder<Raw, Parsed, Key> {
        this.fetcher = object : Fetcher<Raw, Key> {
            override suspend fun fetch(key: Key): Raw {
                return fetcher(key)
            }
        }
        return this
    }

    fun persister(persister: Persister<Raw, Key>): RealStoreBuilder<Raw, Parsed, Key> {
        this.persister = persister
        return this
    }

    fun persister(diskRead: DiskRead<Raw, Key>,
                  diskWrite: DiskWrite<Raw, Key>): RealStoreBuilder<Raw, Parsed, Key> {
        persister = object : Persister<Raw, Key> {
            override suspend fun read(key: Key): Raw? =
                    diskRead.read(key)

            override suspend fun write(key: Key, raw: Raw): Boolean =
                    diskWrite.write(key, raw)
        }
        return this
    }

    fun parser(parser: Parser<Raw, Parsed>): RealStoreBuilder<Raw, Parsed, Key> {
        this.parser = NoKeyParser(parser)
        return this
    }

    fun parser(parser: KeyParser<Key, Raw, Parsed>): RealStoreBuilder<Raw, Parsed, Key> {
        this.parser = parser

        return this
    }

    fun parsers(parsers: List<Parser<Raw, Parsed>>): RealStoreBuilder<Raw, Parsed, Key> {
        TODO("not implemented")
//        this.parsers.clear()
//        for (parser in parsers) {
//            this.parsers.add(NoKeyParser<Key,Raw, Parsed>(parser))
//        }
//        return this
    }

    fun memoryPolicy(memoryPolicy: MemoryPolicy): RealStoreBuilder<Raw, Parsed, Key> {
        this.memoryPolicy = memoryPolicy
        return this
    }

    //Store will backfill the disk cache anytime a record is stale
    //User will still get the stale record returned to them
    fun refreshOnStale(): RealStoreBuilder<Raw, Parsed, Key> {
        stalePolicy = StalePolicy.REFRESH_ON_STALE
        return this
    }

    //Store will try to get network source when disk data is stale
    //if network source throws error or is empty, stale disk data will be returned
    fun networkBeforeStale(): RealStoreBuilder<Raw, Parsed, Key> {
        stalePolicy = StalePolicy.NETWORK_BEFORE_STALE
        return this
    }

    fun open(): Store<Parsed, Key> {
        if (persister == null) {
            persister = NoopPersister.create(memoryPolicy)
        }

        if (parser == null) {
            parser(NoopParserFunc())
        }

//        val multiParser = MultiParser<Key, Raw, Parsed>(parsers)

        val realInternalStore: InternalStore<Parsed, Key> = RealInternalStore(fetcher!!, persister!!, parser!!, memoryPolicy, stalePolicy)

        return RealStore(realInternalStore)
    }

    companion object {

        fun <Raw, Parsed, Key> builder(): RealStoreBuilder<Raw, Parsed, Key> {
            return RealStoreBuilder()
        }
    }
}
