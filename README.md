<table>
  <tr>
    <td>
        <h3>DEPRECATED</h3>
        Store(3) is deprecated. No more development will be taking place. For an up-to-date version,
        please use <a href="https://github.com/dropbox/store">Store(4)</a>. Thanks for all your support!
    </td>
  </tr>
</table>
<br/><br/>

[![Build Status](https://travis-ci.org/NYTimes/Store.svg?branch=feature/rx2)](https://travis-ci.org/NYTimes/Store)

![Store Logo](https://raw.githubusercontent.com/NYTimes/Store/feature/rx2/Images/store-logo.png)

Store is a Java library for effortless, reactive data loading.  

### The Problems:

+ Modern software needs data representations to be fluid and always available.
+ Users expect their UI experience to never be compromised (blocked) by new data loads. Whether an application is social, news, or business-to-business, users expect a seamless experience both online and offline.
+ International users expect minimal data downloads as many megabytes of downloaded data can quickly result in astronomical phone bills.

A Store is a class that simplifies fetching, parsing, storage, and retrieval of data in your application. A Store is similar to the Repository pattern [[https://msdn.microsoft.com/en-us/library/ff649690.aspx](https://msdn.microsoft.com/en-us/library/ff649690.aspx)] while exposing a Reactive API built with [RxJava](https://github.com/ReactiveX/RxJava) that adheres to a unidirectional data flow.

Store provides a level of abstraction between UI elements and data operations.

### Overview

A Store is responsible for managing a particular data request. When you create an implementation of a Store, you provide it with a `Fetcher`, a function that defines how data will be fetched over network. You can also define how your Store will cache data in-memory and on-disk, as well as how to parse it. Since Store returns your data as an `Observable`, threading is a breeze! Once a Store is built, it handles the logic around data flow, allowing your views to use the best data source and ensuring that the newest data is always available for later offline use. Stores can be customized to work with your own implementations or use our included middleware.

Store leverages RxJava and multiple request throttling to prevent excessive calls to the network and disk cache. By utilizing Store, you eliminate the possibility of flooding your network with the same request while adding two layers of caching (memory and disk).

### How to include in your project

###### Include gradle dependency

```
implementation 'com.nytimes.android:store3:3.1.1'
```

###### Set the source & target compatibilities to `1.8`
Starting with Store 3.0, `retrolambda` is no longer used. Therefore to allow support for lambdas the Java `sourceCompatibility` and `targetCompatibility` need to be set to `1.8`

```
android {
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
    ...
}
```

### Fully Configured Store
Let's start by looking at what a fully configured Store looks like. We will then walk through simpler examples showing each piece:

```java
Store<ArticleAsset, Integer> articleStore = StoreBuilder.<Integer, BufferedSource, ArticleAsset>parsedWithKey()
        .fetcher(articleId -> api.getArticleAsBufferedSource(articleId))  // OkHttp responseBody.source()
        .persister(FileSystemPersister.create(FileSystemFactory.create(context.getFilesDir()), pathResolver))
        .parser(GsonParserFactory.createSourceParser(gson, ArticleAsset.Article.class))
        .open();
        
```

With the above setup you have:
+ In-memory caching for rotation
+ Disk caching for when users are offline
+ Parsing through streaming API to limit memory consumption 
+ Rich API to ask for data whether you want cached, new or a stream of future data updates.

And now for the details:

### Creating a Store

You create a Store using a builder. The only requirement is to include a `Fetcher<ReturnType, KeyType>` that returns a `Single<ReturnType>` and has a single method `fetch(key)`


```java
Store<ArticleAsset, Integer> store = StoreBuilder.<>key()
        .fetcher(articleId -> api.getArticle(articleId))  // OkHttp responseBody.source()
        .open();
```

Stores use generic keys as identifiers for data. A key can be any value object that properly implements `toString()`, `equals()` and `hashCode()`. When your `Fetcher` function is called, it will be passed a particular Key value. Similarly, the key will be used as a primary identifier within caches (Make sure to have a proper `hashCode()`!!).

### Our Key implementation - Barcodes
For convenience, we included our own key implementation called a `BarCode`. `Barcode` has two fields `String key` and `String type`

```java
BarCode barcode = new BarCode("Article", "42");
```

When using a `Barcode` as your key, you can use a `StoreBuilder` convenience method

```java
Store<ArticleAsset, BarCode> store = StoreBuilder.<ArticleAsset>barcode()
        .fetcher(articleBarcode -> api.getAsset(articleBarcode.getKey(), articleBarcode.getType()))
        .open();
```


### Public Interface - Get, Fetch, Stream, GetRefreshing

```java
Single<Article> article = store.get(barCode);
```

The first time you subscribe to `store.get(barCode)`, the response will be stored in an in-memory cache. All subsequent calls to `store.get(barCode)` with the same Key will retrieve the cached version of the data, minimizing unnecessary data calls. This prevents your app from fetching fresh data over the network (or from another external data source) in situations when doing so would unnecessarily waste bandwidth and battery. A great use case is any time your views are recreated after a rotation, they will be able to request the cached data from your Store. Having this data available can help you avoid the need to retain this in the view layer.


So far our Store’s data flow looks like this:
![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-1.jpg)


By default, 100 items will be cached in memory for 24 hours. You may pass in your own instance of a Guava Cache to override the default policy.


### Busting through the cache

Alternatively you can call `store.fetch(barCode)` to get an `Observable` that skips the memory (and optional disk cache).


Fresh data call will look like: `store.fetch()`
![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-2.jpg)


In the New York Times app, overnight background updates use `fetch()` to make sure that calls to `store.get()` will not have to hit the network during normal usage. Another good use case for `fetch()` is when a user wants to pull to refresh.


Calls to both `fetch()` and `get()` emit one value and then call `onCompleted()` or throw an error.


### Stream
For real-time updates, you may also call `store.stream()` which returns an `Observable` that emits each time a new item is added to the Store. You can think of stream as an Event Bus-like feature that allows you to know when any new network hits happen for a particular Store. You can leverage the Rx operator `filter()` to only subscribe to a subset of emissions.


### Get Refreshing
There is another special way to subscribe to a Store: `getRefreshing(key)`. This method will subscribe to `get()` which returns a single response, but unlike `get()`, `getRefreshing(key)` will stay subscribed. Anytime you call `store.clear(key)` anyone subscribed to `getRefreshing(key)` will resubscribe and force a new network response.


### Inflight Debouncer

To prevent duplicate requests for the same data, Store offers an inflight debouncer. If the same request is made within a minute of a previous identical request, the same response will be returned. This is useful for situations when your app needs to make many async calls for the same data at startup or when users are obsessively pulling to refresh. As an example, The New York Times news app asynchronously calls `ConfigStore.get()` from 12 different places on startup. The first call blocks while all others wait for the data to arrive. We have seen a dramatic decrease in the app's data usage after implementing this inflight logic.


### Adding a Parser

Since it is rare for data to arrive from the network in the format that your views need, Stores can delegate to a parser by using a `StoreBuilder.<BarCode, BufferedSource, Article>parsedWithKey()`

```java
Store<Article, Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
        .fetcher(articleId -> api.getArticle(articleId)) 
        .parser(source -> {
            try (InputStreamReader reader = new InputStreamReader(source.inputStream())) {
                return gson.fromJson(reader, Article.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
        .open();
```

Our updated data flow now looks like this:

`store.get()` -> ![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-3.jpg)


### Middleware - GsonSourceParser

There are also separate middleware libraries with parsers to help in cases where your fetcher is a `Reader`, `BufferedSource` or `String` and your parser is Gson:
- GsonReaderParser
- GsonSourceParser
- GsonStringParser

These can be accessed via a Factory class (`GsonParserFactory`).

Our example can now be rewritten as:

```java
Store<Article, Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
        .fetcher(articleId -> api.getArticle(articleId)) 
        .parser(GsonParserFactory.createSourceParser(gson, Article.class))
        .open();
```

In some cases you may need to parse a top level JSONArray, in which case you can provide a `TypeToken`.

```java
Store<List<Article>, Integer> store = StoreBuilder.<Integer, BufferedSource, List<Article>>parsedWithKey()
        .fetcher(articleId -> api.getArticles()) 
        .parser(GsonParserFactory.createSourceParser(gson, new TypeToken<List<Article>>() {}))
        .open();  
```

Similarly we have a middleware artifact for Moshi & Jackson too!


### Disk Caching

Stores can enable disk caching by passing a `Persister` into the builder. Whenever a new network request is made, the Store will first write to the disk cache and then read from the disk cache.


Now our data flow looks like:
`store.get()` -> ![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-5.jpg)


Ideally, data will be streamed from network to disk using either a `BufferedSource` or `Reader` as your network raw type (rather than `String`).

```java
Store<Article, Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
        .fetcher(articleId -> api.getArticles())
        .persister(new Persister<BufferedSource>() {
            @Override
            public Maybe<BufferedSource> read(Integer key) {
                if (dataIsCached) {
                    return Observable.fromCallable(() -> userImplementedCache.get(key));
                } else {
                    return Observable.empty();
                }    
            }
    
            @Override
            public Single<Boolean> write(BarCode barCode, BufferedSource source) {
                userImplementedCache.save(key, source);
                return Single.just(true);
            }
        })
        .parser(GsonParserFactory.createSourceParser(gson, Article.class))
        .open();
```

Stores don’t care how you’re storing or retrieving your data from disk. As a result, you can use Stores with object storage or any database (Realm, SQLite, CouchDB, Firebase etc). The only requirement is that data must be the same type when stored and retrieved as it was when received from your `Fetcher`. Technically, there is nothing stopping you from implementing an in memory cache for the “persister” implementation and instead have two levels of in memory caching--one with inflated and one with deflated models, allowing for sharing of the “persister” cache data between stores.


**Note**: When using a Parser and a disk cache, the Parser will be called AFTER fetching from disk and not between the network and disk. This allows your persister to work on the network stream directly.


If using SQLite we recommend working with [SqlBrite](https://github.com/square/sqlbrite). If you are not using SqlBrite, an `Observable` can be created rather simply with `Observable.fromCallable(() -> getDBValue())`

### Middleware - SourcePersister & FileSystem

We've found the fastest form of persistence is streaming network responses directly to disk. As a result, we have included a separate library with a reactive FileSystem which depends on Okio `BufferedSource`s. We have also included a `FileSystemPersister` which will give you disk caching and works beautifully with `GsonSourceParser`. When using the `FileSystemPersister` you must pass in a `PathResolver` which will tell the file system how to name the paths to cache entries. 

Now back to our first example:

```java
Store<Article, Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
        .fetcher(articleId -> api.getArticles(articleId)) 
        .persister(FileSystemPersister.create(FileSystemFactory.create(context.getFilesDir()), pathResolver))
        .parser(GsonParserFactory.createSourceParser(gson, String.class))
        .open();
```

As mentioned, the above builder is how we work with network operations at the New York Times. With the above setup you have:
+ Memory caching with Guava Cache
+ Disk caching with FileSystem (you can reuse the same file system implementation for all stores)
+ Parsing from a BufferedSource (to an Article in our case) with Gson
+ In-flight request management
+ Ability to get cached data or bust through your caches (`get()` vs. `fetch()`)
+ Ability to listen for any new emissions from network (stream)
+ Ability to be notified and resubscribed when caches are cleared (helpful for times when you need to do a POST request and update another screen, such as with `getRefreshing()`)

We recommend using the above builder setup for most Stores. The SourcePersister implementation has a tiny memory footprint because it will stream bytes from network to disk and then from disk to parser. The streaming nature of Stores allows us to download dozens of 1mb+ json responses without worrying about OOM on low-memory devices. As mentioned above, Stores allow us to do things like calling `configStore.get()` a dozen times asynchronously before our Main Activity finishes loading without blocking the main thread or flooding our network.

### RecordProvider
If you'd like your Store to know about disk data staleness, you can have your `Persister` implement `RecordProvider`.  After doing so you can configure your Store to work in one of two ways:

```java
store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .refreshOnStale()
                .open();
``` 

`refreshOnStale()` will backfill the disk cache anytime a record is stale. The user will still get the stale record returned to them.

Or alternatively:

```java
store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .networkBeforeStale()
                .open();
```   

`networkBeforeStale()` - Store will try to get network source when disk data is stale. If the network source throws an error or is empty, stale disk data will be returned.


### Subclassing a Store

We can also subclass a Store implementation (`RealStore<T>`):

```java
public class SampleStore extends RealStore<String, BarCode> {
    public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}
```

Subclassing is useful when you’d like to inject Store dependencies or add a few helper methods to a store:

```java
public class SampleStore extends RealStore<String, BarCode> {
   @Inject
   public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}
```


### Artifacts

**CurrentVersion = 3.1.1**

+ **Cache** Cache extracted from Guava (keeps method count to a minimum)

  ```groovy
  implementation 'com.nytimes.android:cache3:CurrentVersion'
  ```
  
+ **Store** This contains only Store classes and has a dependency on RxJava + the above cache.  

  ```groovy
  implementation 'com.nytimes.android:store3:CurrentVersion'
  ```
  
+ **Store-Kotlin** Store plus a couple of added Kotlin classes for more idiomatic usage.

    ```groovy
    implementation 'com.nytimes.android:store-kotlin3:CurrentVersion'
    ```
    
+ **Middleware** Sample Gson parsers, (feel free to create more and open PRs) 

    ```groovy
    implementation 'com.nytimes.android:middleware3:CurrentVersion'
    ```
    
+ **Middleware-Jackson** Sample Jackson parsers, (feel free to create more and open PRs)

    ```groovy
    implementation 'com.nytimes.android:middleware-jackson3:CurrentVersion'
    ```
    
+ **Middleware-Moshi** Sample Moshi parsers, (feel free to create more and open PRs)

    ```groovy
    implementation 'com.nytimes.android:middleware-moshi3:CurrentVersion'
    ```
    
+ **File System** Persistence Library built using [Okio](https://github.com/square/okio) Source/Sink + Middleware for streaming from Network to FileSystem 

  ```groovy
  implementation 'com.nytimes.android:filesystem3:CurrentVersion'
  ```

### Sample Project

See the app for example usage of Store. Alternatively, the [Wiki](https://github.com/NYTimes/Store/wiki) contains a set of recipes for common use cases
+ Simple Example: Retrofit + Store
+ Complex Example: BufferedSource from Retrofit (Can be [OkHttp](https://github.com/square/okhttp) too) + our FileSystem + our GsonSourceParser

### Talks
+ [DroidCon Italy](https://youtu.be/TvsOsgd0--c)
+ [Android Makers](https://www.youtube.com/watch?time_continue=170&v=G1MebI2k9aA)

### Community projects

+ https://github.com/stoyicker/master-slave-clean-store: An offline-first Master-Slave project with scroll-driven pagination using Store for the data layer.
+ https://github.com/benoberkfell/cat-rates: [Ben Oberkfell's 360AnDev talk, "Android Architecture for the Subway"](https://academy.realm.io/posts/360-andev-2017-ben-oberkfell-android-architecture-offline-first/) illustrates using Store for caching server responses
