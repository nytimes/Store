[![Build Status](https://travis-ci.org/NYTimes/Store.svg?branch=master)](https://travis-ci.org/NYTimes/Store)


# Store

Android library for async data loading from network or 2 levels of caching.  NOTE: While this library is production ready it is still in early stages and some apis may be renamed/changed.

### The Problems:

+ Modern Android Apps need their data representations to be fluid and always available.
+ Users expect their UI experience to never be compromised (blocked) by new data loads. Whether an application is a social, news, or business-to-business app, users expect a seamless experience both online and offline.
+ International users expect minimal data downloads as mbs of download can quickly turn into astronomical phone bills.

A Store is a class that simplifies fetching, parsing, storage, and retrieval of data in your application. A Store is similar to the Repository pattern [[https://msdn.microsoft.com/en-us/library/ff649690.aspx](https://msdn.microsoft.com/en-us/library/ff649690.aspx)] while exposing a Reactive API built with RxJava that adheres to a unidirectional data flow.

Store provides a level of abstraction between our UI elements and data operations.

### Overview

A Store is responsible for managing a particular data request in an application. When you create an implementation of a Store, you provide it with a `Fetcher`. Additionally, you can define how your Store will cache data in-memory and on-disk, as well as how to parse it. Since you'll be getting back an Observable of your data, threading is a breeze! Once a store is built, it will handle the logic around data flow, allowing your views to use the best data source and ensuring that the newest data is always available for later offline use. Stores can be customized to work with your own implementations or use our included middleware.

Store leverages RxJava and multiple request throttling to prevent excessive calls to the network and disk cache. By utilizing our library, you eliminate the possibility of flooding your network with the same request while adding 2 layers of caching (memory + disk).

### Fully Configured Store
Let's start by looking at what a fully configured store looks like, we will then walk through simpler examples building up functionality:
```java
Store<ArticleAsset, Integer> articleStore = StoreBuilder.<Integer, BufferedSource, ArticleAsset>parsedWithKey()
                .fetcher(articleId -> api.getArticleAsBufferedSource(articleId))  //OkHttp responseBody.source()
                .persister(FileSystemPersister.create(FileSystemFactory.create(context.getFilesDir())))
                .parser(GsonParserFactory.createSourceParser(gson, ArticleAsset.Article.class))
                .open();
	      
```

With the above setup you have:
+ In Memory Caching for rotation
+ Disk caching for when you're offline
+ Parsing through streaming api to keep memory consumption to a minimum
+ Rich API to ask for data whether you want cached/new or a stream of future data updates.

And now for the details:

### Creating a Store

Create a store using a builder, the only requirement is to include a `.Fetcher<ReturnType,KeyType>` that returns an Observable<ReturnType> and has a single method `fetch(key)`


``` java
        Store<ArticleAsset, Integer> store = StoreBuilder.<ArticleAsset,Integer>key()
                .fetcher(articleId -> api.getArticle(articleId))  //OkHttp responseBody.source()
                .open();
```
Stores use generic keys as identifiers for data. A Key can be any value object that properly implements toString and equals and hashCode. When your Fetcher function is called, it will be passed a particular Key value. Similarly, the key will be used as a primary identifier within caches (Make sure to have a proper hashCode!!) 

### Our Key implementation - Barcodes
For convenience we included our own key implementation called a BarCode. Barcode has 2 fields `String key and String type`
``` java
BarCode barcode = new BarCode("Article", "42");
```
When using a Barcode as your key, you can use a StoreBuilder convenience method
``` java
 Store<ArticleAsset, Integer> store = StoreBuilder.<ArticleAsset>barcode()
                .fetcher(articleBarcode -> api.getAsset(articleBarcode.getKey(),articleBarcode.getType()))
                .open();
```



### Public Interface - Get, Fetch, Stream, GetRefreshing

```java
Observable<Article> article = store.get(barCode);
```

The first time you subscribe to `store.get(barCode)`, the response will be stored in an in-memory cache. All subsequent calls to `store.get(barCode)` with the same Key will retrieve the cached version of the data, minimizing unnecessary data calls. This prevents your app from fetching fresh data over the network (or from another external data source) in situations when doing so would unnecessarily waste bandwidth and battery. A great use case is any time your views get recreated after a rotation, they will be able to request the cached data from your store. Having your data available has helped us retain less without or view layer


So far our Store’s data flow looks like this:
![Simple Store Flow](https://github.com/nytm/Store/blob/master/Images/store-1.jpg)


By default 100 items will be cached in memory for 24 hours. You may pass in your own instance of a Guava Cache to override the default policy.


### Busting through the cache

Alternatively you can call `store.fetch(barCode)` to get an Observable that skips the memory (and optional disk cache).


Fresh data call will look like: `store.fetch()`
![Simple Store Flow](https://github.com/nytm/Store/blob/master/Images/store-2.jpg)


Overnight background updates within our app use `fetch` to make sure that calls to `store.get()` will not have to hit network during normal usage. Another good use case for `fetch` is pull to refresh.


Calls to both `fetch()` and `get()` emit one value and then call `onCompleted()` or throw an error


### Stream
You may also call `store.stream()` which returns an Observable that emits each time a new item was added to the store. Think of stream as an Event Bus-like feature that allows you to know when any new network hits happen for a particular store. You can leverage the Rx operator `filter()` to only subscribe to a subset of emissions.

### Get Refreshing
There is 1 more special way to subscribe to a Store - getRefreshing(key).  Get Refreshing will subscribe to get() which returns a single response, unlike Get, Get Refreshing will stay subscribed.  Anytime you call store.clear(key) anyone subscribe to getRefreshing(key) will resubscribe and force a new network response.


### Inflight Debouncer

There is an inflight debouncer as well to prevent duplicative requests for the same data. If same request is made within a minute of a previous identical request, the same response will be returned (useful for when your app has many async calls for the same data at startup or for when users are obsessively pulling to refresh). As an example, on start our app asynchronously calls `ConfigStore.get()` from 12 different places. The first call blocks while all others wait for the data to arrive. We have seen dramatic decrease in the data usage of our app since implementing the above in flight logic.


### Adding a Parser

Since it is rare that data comes from the network in the format that your views need, Stores can delegate to a parser. by using a `StoreBuilder.<BarCode, BufferedSource, Article>parsedWithKey()

```java
Store<Article,Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
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

`store.get()` -> ![Simple Store Flow](https://github.com/nytm/Store/blob/master/Images/store-3.jpg)



### Middleware - GsonSourceParser

There are also seperate middleware libraries with parsers to help in cases where your fetcher is a Reader, BufferedSource or String and your parser is Gson:
- GsonReaderParser
- GsonSourceParser
- GsonStringParser

These can be accessed via a Factory class (GsonParserFactory).

Our example can now be rewritten as:
```java
Store<Article,Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
        .fetcher(articleId -> api.getArticle(articleId)) 
                .parser(GsonParserFactory.createSourceParser(gson, Article.class))
                .open();
```

In some cases you may need to parse a top level JSONArray, in which case you can provide a TypeToken.
```java
Store<List<Article>,Integer> store = StoreBuilder.<Integer, BufferedSource, List<Article>>parsedWithKey()
        .fetcher(articleId -> api.getArticles()) 
                .parser(GsonParserFactory.createSourceParser(gson, new TypeToken<List<Article>>() {}))
                .open();
		
		
Similar we have a middleware artifact for Moshi & Jackson too!
```

### Disk Caching

Stores can enable disk caching by passing in a Persister to the builder. Whenever a new network request is made, it will first write to the disk cache and then read from the disk cache.


Now our data flow looks like:
`store.get()` -> ![Simple Store Flow](https://github.com/nytm/Store/blob/master/Images/store-5.jpg)



 Ideally, data will be streamed from network to disk using either a BufferedSource or Reader as your network raw type (rather than String).

```java
Store<Article,Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
        .fetcher(articleId -> api.getArticles()) 
           .persister(new Persister<BufferedSource>() {
             @Override
             public Observable<BufferedSource> read(Integer key) {
               if (dataIsCached) {
                 return Observable.fromCallable(() -> userImplementedCache.get(key));
               } else {
                 return Observable.empty();
               }
             }
       
             @Override
             public Observable<Boolean> write(BarCode barCode, BufferedSource source) {
               userImplementedCache.save(key, source);
               return Observable.just(true);
             }
           })
           .parser(GsonParserFactory.createSourceParser(gson, Article.class))
           .open();
```

Stores don’t care how you’re storing or retrieving your data from disk. As a result, you can use stores with object storage or any database (Realm, SQLite, CouchDB, Firebase etc). The only requirement is that you can store and retrieve the data using the same type as your Fetcher. Technically there is nothing stopping you from implementing an in memory cache for the “persister” implementation and instead have 2 levels of in memory caching (one with inflated and one with deflated models, allowing for sharing of the “persister” cache data between stores).


**Note**: When using a Parser and a disk cache, the parser will be called AFTER fetching from disk and not between the network and disk allow your persister to work on the network stream directly.


If using SQLite we recommend working with SqlBrite. If you are not using SqlBrite an Observable can be created rather simply with `Observable.fromCallable(() -> getDBValue())`

### Middleware - SourcePersister & FileSystem

We've found the fastest form of persistence is streaming network responses directly to disk. As a result, we have included a seperate lib with a reactive FileSystem which depends on Okio BufferedSources. We have also included a FileSystemPersister which will give you disk caching and works beautifully with GsonSourceParser. Now we are back to our first example:

```java
Store<Article,Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
               .fetcher(articleId -> api.getArticles(articleId)) 
               .persister(FileSystemPersister.create(FileSystemFactory.create(context.getFilesDir())))
               .parser(GsonParserFactory.createSourceParser(gson, String.class))
               .open();
```

As mentioned, the above builder is how we work with network operations at New York Times. With the above setup you have:
+ Memory caching with Guava Cache
+ Disk caching with FileSystem (you can reuse the same file system impl for all stores)
+ Parsing from a BufferedSource to a <T> (Article in our case) with Gson
+ in-flight request management
+ Ability to get cached data or bust through your caches (get vs fresh)
+ Ability to listen for any new emissions from network (stream)
+ Ability to be notified and resubscribed when caches are cleared,nice for when you need to do a post request and update another screen (getRefreshing)

We recommend using the above setup of the builder for most Stores. The SourcePersister implementation has a tiny memory footprint as it will stream bytes from network to disk and then from disk to parser. The streaming nature of our stores allows us to download dozens of 1mb+ json responses without worrying about OOM on low-memory devices. As mentioned above, Stores allow us to do things like calling `configStore.get()` a dozen times asynchronously before our Main Activity finishes loading without blocking the main thread or flooding our network.

### RecordProvider
If you'd like your store to be aware of the staleness of disk data you can have your `Persister` implement `RecordProvider`.  After doing so you can configure your Store to work in one of two ways:
store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .refreshOnStale()
                .open();
		
refreshOnStale - will backfill the disk cache anytime a record is stale, User will still get the stale record returned to them

Or alternatively
        store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .networkBeforeStale()
                .open();
		
networkBeforeStale - Store will try to get network source when disk data is stale. if network source throws error or is empty, stale disk data will be returned


### Subclassing a Store

We can also subclass a Store implementation (RealStore<T>):

```java
public class SampleStore extends RealStore<String, BarCode> {
    public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}

Subclassing is useful for when you’d like to inject Store dependencies or add a few helper methods to a store:

```java
public class SampleStore extends RealStore<String, BarCode> {
   @Inject
   public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}
```


### Artifacts
Note: Release is in Sync with current state of master (not develop) branch

CurrentVersion = **2.0.0**

+ **Cache** Cache extracted from Guava (keeps method count to a minimum)

	```groovy
	compile 'com.nytimes.android:cache:CurrentVersion'
	```
+ **Store** This contains only Store classes and has a dependecy on RxJava + the above cache.  

	```groovy
	compile 'com.nytimes.android:store:CurrentVersion'
	```
+ **Middleware** Sample Gson parsers, (feel free to create more and open PRs) 

    ```groovy
    compile 'com.nytimes.android:middleware:CurrentVersion'
    ```
+ **Middleware-Jackson** Sample Jackon parsers, (feel free to create more and open PRs)

    ```groovy
    compile 'com.nytimes.android:middleware:-jackson:CurrentVersion'
    ```
+ **Middleware-Moshi** Sample Moshi parsers, (feel free to create more and open PRs)

    ```groovy
    compile 'com.nytimes.android:middleware-moshi:CurrentVersion'
    ```
+ **File System** Persistence Library built using OKIO Source/Sink + Middleware for streaming from Network to FileSystem 

	```groovy
	compile 'com.nytimes.android:filesystem:CurrentVersion'
	```


### Sample Project

See app for example usage of Store. Alternatively the Wiki contains a set of recipes for common use cases
+ Simple Example: Retrofit + Store
+ Complex Example: BufferedSource from Retrofit (Can be OKHTTP too) + our FileSystem + our GsonSourceParser
