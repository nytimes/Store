# Store

Android library for async data loading from multiple sources

### The Problems:

+ Modern Android Apps need their data representations to be fluid and always available.
+ Users expect their UI experience to never be compromised (blocked) by new data loads. Whether an application is a social, news, or business-to-business app, users expect a seamless experience both online and offline.
+ International users expect minimal data downloads as mbs of download can quickly turn into astronomical phone bills.

A Store is a class that simplifies fetching, parsing, storage, and retrieval of data in your application. A Store is similar to the Repository pattern [[https://msdn.microsoft.com/en-us/library/ff649690.aspx](https://msdn.microsoft.com/en-us/library/ff649690.aspx)] while exposing a Reactive API built with RxJava that adheres to a unidirectional data flow.

Store provides a level of abstraction between our UI elements and data operations.

### Overview

A Store is responsible for managing a particular data request in an application. When you create an implementation of a Store, you provide it with a Fetcher&lt;link&gt;. Additionally, you can define how your Store will cache data in-memory and on-disk, as well as how to parse it. Since you'll be getting back an Observable of your data, threading is a breeze! Once a store is  built, it will handle the logic around data flow, allowing your views to use the best data source and ensuring that the newest data is always available for later offline use. Stores can be customized to work with your own implementations or use our included middleware.

Store leverages RxJava and multiple request throttling to prevent excessive calls to the network and disk cache. By utilizing our library, you eliminate the possibility of flooding your network with the same request while adding 2 layers of caching (memory + disk).

### Fully Configured Store
Let's start by looking at what a fully configured store looks like, we will then walk through simpler examples building up functionality:
```java
Store<Foo> Store = ParsingStoreBuilder.<BufferedSource, String>builder()
               .fetcher(this::ResponseAsSource)  //responseBody.source()
               .persister(new SourcePersister(new FileSystemImpl(context.getFilesDir())))
               .parser(new GsonSourceParser<>(gson, Foo.class))
               .open();
	      
```

With the above setup you have:
+ In Memory Caching
+ Disk caching
+ Parsing through streaming api
+ Ability to get cached data or bust through your caches

And now for the details:

### Creating a Store

Create a store using a builder, the only requirement is to include a `.fetcher()`.


``` java
Store<Article> ArticleStore = StoreBuilder.<String>builder()
               .nonObservableFetcher(barCode -> api.getArticle(barcode.getValue()))
               .open();


Store<Article> ArticleStore = StoreBuilder.<String>builder()
               .fetcher(barCode -> retrofitApi.getArticleObservable(barcode.getValue()))
               .open();
```

### Barcodes

``` java
Barcode barcode = new Barcode("Article", "42");
```

Stores use Barcodes as identifiers for data. A Barcode is a class that holds two strings, type and value. The two values act as unique identifiers for your data. When your Fetcher function is called, it will be passed the Barcode.  Similarly, the barcode will be used as a key in your cache(s).


### Public Interfaces for Accessing Data - Get, Fresh, Stream

```java
Observable<Article> article= store.get(barCode);
```

The first time you subscribe to `store.get(barcode)`, the response will be stored in an in-memory cache using the Barcode as a key. All subsequent calls to `store.get(barcode)` will retrieve the cached version of the data, minimizing unnecessary data calls. This prevents your app from fetching fresh data over the network (or from another external data source) in situations when doing so would unnecessarily waste bandwidth and battery. A great use case is any time your views get recreated after a rotation, they will be able to request the cached data from your store.  Having your data available has helped us retain less without or view layer


So far our Store’s data flow looks like this:
![Simple Store Flow](https://github.com/nytm/Store/blob/master/Images/store-1.jpg)
`store.get()`  -> return memory cached version if exists, otherwise -> fetch new networkResponse -> save in memory -> return newly cached response from memory


By default 100 items will be cached in memory for 24 hours. You may pass in your own instance of a Guava Cache to override the default policy.  


### Busting through the cache

Alternatively you can call `store.fresh(barcode)` to get an Observable that skips the memory (and optional disk cache).


Fresh data call will look like: `store.fresh()`
![Simple Store Flow](https://github.com/nytm/Store/blob/master/Images/store-2.jpg)


Overnight background updates within our app us fresh to make sure that calls to `store.get()` will not have to hit network during normal usage.  Another good use case for fresh is pull to refresh.


Calls to both `fresh()` and `get()` emit one value and then call `onCompleted()` or throw an error


### Stream
You may also call `store.stream(barcode)` which returns an Observable emitting the response for your barcode and then stays subscribed to receive any new items emitted for any calls to that store (for any barcode).  Think of stream as an Event Bus-like feature that allows you to know when any new network hits happen for a particular store. You can leverage the Rx operator `filter()` to only subscribe to a subset of emissions.


### Inflight Debouncer

There is an inflight debouncer as well to prevent duplicative requests for the same data. If same request is made within a minute of a previous identical request, the same response will be returned (useful for when your app has many async calls for the same data at startup or for when users are obsessively pulling to refresh).  As an example, on start our app asynchronously calls `ConfigStore.get()` from 12 different places.  The first call blocks while all others wait for the data to arrive. We have seen dramatic decrease in the data usage of our app since implementing the above in flight logic.


### Adding a Parser

Since it is rare that data comes from the network in the format that your views need, Stores can delegate to a parser. by using a `ParsingStoreBuilder<T,V>` rather than a `StoreBuilder<T>.`  ParsingStoreBuilder has an additional method `parser()` which can take a Parser<Raw, Parsed>

```
Store<Article> Store =
ParsingStoreBuilder.<BufferedSource, String>builder()
      .nonObservableFetcher(barCode -> source)) //okhttp responseBody.source()
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

There is also a seperate middleware lib with parsers to help in cases where your fetcher is a Reader, BufferedSource or String and your parser is Gson:
GsonReaderParser,
GsonSourceParser,
GsonStringParser.  


Our example can now be rewritten as:
```java
Store<Article> Store = ParsingStoreBuilder.<BufferedSource, Article>builder()
                .nonObservableFetcher(this::getResponse)
       .parser(new GsonSourceParser<>(gson, Article.class))
       .open();
```

### Disk Caching

Stores can enable disk caching by passing in a Persister to the builder.  Whenever a new network request is made, it will first write to the disk cache and then read from the disk cache.


Now our data flow looks like:
`store.get()` -> ![Simple Store Flow](https://github.com/nytm/Store/blob/master/Images/store-5.jpg)



 Ideally, data will be streamed from network to disk using either a BufferedSource or Reader as your network raw type (rather than String).

```java
       Store<String> Store = ParsingStoreBuilder.<BufferedSource, String>builder()
                              .nonObservableFetcher(this::ResponseAsSource)  //okhttp responseBody.source()
                .persister(new Persister<BufferedSource>() {
                    @Override
                    public Observable<BufferedSource> read(BarCode barCode) {
                      if(dataIsCached)
  return Observable.fromCallable(()->userImplementedCache.get(barcode));
        	      else{
			  Return Observable.empty();
       		     }

                    @Override
                    public Observable<Boolean> write(BarCode barCode, BufferedSource source) {
	                       userImplementedCache.save(barcode,source)
         			return Observable.just(true);
                    }
                })
		.parser(new GsonSourceParser<>(gson, String.class))
                .open();
```

Stores don’t care how you’re storing or retrieving your data from disk. As a result, you can use stores with object storage or any database (realm, sql lite, couchDB,firebase etc).  The only requirement is that you can store and retrieve the data using the same type as your Fetcher.  Technically there is nothing stopping you from implementing an in memory cache for the “persister” implementation and instead have 2 levels of in memory caching (one with inflated and one with deflated models, allowing for sharing of the “persister” cache data between stores)


**Note**: When using a Parser and a disk cache, the parser will be called AFTER fetching from disk and not between the network and disk allow your persister to work on the network stream directly.


If using SqlLite we recommend working with SqlBrite.  If you are not using SqlBrite an Observable can be created rather simply with `Observable.fromCallable(() -> getDBValue())`

### Middleware - SourcePersister & FileSystem

We've found the fastest form of persistence is streaming network responses directly to disk. As a result, we have included a seperate lib with a reactive FileSystem which depends on OKIO BufferedSources. We have also included a SourcePersister which will give you disk caching and works beautifully with GsonSourceParser.  Now we are back to our first example:

```
Store<String> Store = ParsingStoreBuilder.<BufferedSource, String>builder()
               .nonObservableFetcher(this::ResponseAsSource)  //okhttp responseBody.source()
               .persister(new SourcePersister(new FileSystemImpl(context.getFilesDir())))
   .parser(new GsonSourceParser<>(gson, String.class))
               .open();
```

As mentioned, the above builder is how we work with network operations at New York Times.  With the above setup you have:
+ Memory caching with Guava Cache
+ Disk caching with FileSystem (you can reuse the same file system impl for all stores)
+ Parsing from a BufferedSource to a <T> (String in our case) with Gson
+ in-flight request management
+ Ability to get cached data or bust through your caches (get vs fresh)


We recommend using the above setup of the builder for most Stores.  The SourcePersister implementation has a tiny memory footprint as it will stream bytes from network to disk and then from disk to parser. The streaming nature of our stores allows us to download dozens of 1mb+ json responses without worrying about OOM on low-memory devices.  As mentioned above, Stores allow us to do things like calling `configStore.get()` a dozen times asynchronously before our Main Activity finishes loading without blocking the main thread or flooding our network.

### Subclassing a Store

We can also subclass a Store implementation (RealStore<T>):

```java
public class SampleStore extends RealStore<String> {
   public SampleStore(Fetcher<String> f, Persister<String> p) {
       super(f, p);
   }
}
```
Or with a parser:

```java
public class SampleStore extends RealStore<String> {
   public SampleStore(Fetcher<BufferedSource> fetcher,
                      Persister<BufferedSource> persister,
                      Parser<BufferedSource,String> parser) {
       super(fetcher, persister, parser);
   }
}
```

Subclassing is useful for when you’d like to inject Store dependencies or add a few helper methods to a store:

```java
public class SampleStore extends RealStore<String> {
   @Inject
   public SampleStore(Fetcher<String> f, Persister<String> p) {
       super(f, p);
   }
}
```


### Artifacts
Since this is android, we have split Store into 4 artifacts:
+ **Cache**  Cache extracted from Guava (~200 methods) 

	`compile 'com.nytimes.android:cache:1.0.1'`
+ **Store**. This contains only Store classes and has a dependecy on RxJava + the above cache.  

	`compile 'com.nytimes.android:store:1.0.1'`
+ **Middleware** Sample gson parsers, (feel free to create more and open PRs) 

	`compile 'com.nytimes.android:middleware:1.0.1'`
+ **File System** Persistence Library built using OKIO Source/Sink + Middleware for streaming from Network to FileSystem 

	`compile 'com.nytimes.android:filesystem:1.0.1'`


### Sample Project

See app for example usage of Store.
+ Simple Example: Retrofit + Store
+ Complex Example: BufferedSource from Retrofit (Can be OKHTTP too) + our FileSystem + our GsonSourceParser
