# store-kotlin

Store with bindings for Kotlin.

## Usage

StoreBuilder:

```kotlin
FluentStoreBuilder.barcode(myFetcher) {
    persister = myPersister
    memoryPolicy = myMemoryPolicy
    stalePolicy = myStalePolicy
}
FluentStoreBuilder.key().fetcher(myFetcher) {
    persister = myPersister
    memoryPolicy = myMemoryPolicy
    stalePolicy = myStalePolicy
}
FluentStoreBuilder.parsedWithKey<Key, Raw, Parsed>(myFetcher) {
    persister = myPersister
    memoryPolicy = myMemoryPolicy
    stalePolicy = myStalePolicy
    parser = myParser
    parsers = myParsers
}
```

MemoryPolicyBuilder:

```kotlin
FluentMemoryPolicyBuilder.build {
    expireAfterWrite = expireAfterWriteValue
    expireAfterAccess = expireAfterWriteAccess
    expireAfterTimeUnit = expireAfterTimeUnitValue
    memorySize = maxSizeValue
}
```

And you can always omit the configuration block if you're happy with the defaults!
