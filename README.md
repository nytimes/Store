# Store
Android Library for Async Loading from multiple data sources
**Store Grand Opening**

Problem:

Modern Android Apps need to be fluid always available representations of the data that is backing them.  User&#39;s expect their UI experience to never be compormissed (blocked) by any new data loads.  Whether you are a social, news, or business to business application, users will expect seamlessly when they are on or offline.  Being a world phone, mbs of download can quickly turn into astronomical phone bills.

Here at The New York Times we strive to deliver our users the absolute best experience we can.  On Android that meant creating a offline first, reactive architecture which minimizes how much data our app uses.  We leverage open source libraries such as Dagger, Rxjava, OkHTTP, OKIO as well as patterns like MVP. We then tie it all together with a simple library we have developed called Store.

A Store is a class that simplifies fetching, parsing, storage, and retrieval of data in your application. A Store is similar to the Repository pattern [[https://msdn.microsoft.com/en-us/library/ff649690.aspx](https://msdn.microsoft.com/en-us/library/ff649690.aspx)] while being exposing a Reactive API built with RxJava and adhering to a unidirectional data flow.

The New York Times Android team uses Store as the abstraction between our UI elements and data operations. Stores simplify the logic around data fetching for offline and online users, helping us be an offline-first application. Stores also help boost app performance by preventing unnecessary network calls or disk reads. The architecture of our flagship news app is made up of immutable data (M)odels, Custom (V)iews, (P)resenters and (S)tores -  which we like to call MVPS.

Overview

A Store is responsible for managing a particular data request in our application. When we create an implementation of a Store, we provide it with a Fetcher&lt;link&gt;. Additionally, you can define how your Store will save data in-memory and on-disk, as well as how to parse it. Since you&#39;ll be getting back an Observable of your data, threading is a breeze! Once stores are built, Store handles the logic around data flow, allowing your views to show the best data source and ensuring that the newest data is always available for later offline use. Stores can be customized to work with your own implementations or use our including middleware.

Store leverages RxJava and multiple request throttling to prevent excessive calls to the network and disk cache.  By utilizing our library, you eliminate the possibility of flooding your network with the same request while adding 2 layers of caching (memory + disk).

Creating a Store


Create a store using a builder, the only requirement is to include a .fetcher().


Store<Article> ArticleStore = StoreBuilder.<String>builder()
               .nonObservableFetcher(barCode -> api.getArticle(barcode.getValue()))
               .open();


Store<Article> ArticleStore = StoreBuilder.<String>builder()
               .fetcher(barCode -> retrofitApi.getArticleObservable(barcode.getValue()))
               .open();


Barcodes
Barcode barcode = new Barcode("Article", "42");


Stores use Barcodes as identifiers for data. A Barcode is a class that holds two strings, type and value. The two values act as a unique identifiers for your data. When your Fetcher functions is called, it will be passed the Barcode.  Similar the barcode will be used as a key in your cache(s).


Public Interfaces for Accessing Data - Get, Fresh, Stream


Observable<Article> article= store.get(barCode);


The first time you subscribe using store.get(barcode), the response will be stored within an in-memory cache using the Barcode as a key. All subsequent calls to store.get(barcode) will retrieve the cached version of the data, minimizing unnecessary data calls. This prevents your app from fetching fresh data over the network (or from another external data source) in situations when doing so would unnecessary wasting bandwidth and battery. A great use case is any time your views get recreated after a rotation they will be able to request the cached data from your store.  Having your data available has helped us retain less without or view layer


So far our Store’s data flow looks like this:
 store.get()->return memory cached version if exists, otherwise->fetch new networkResponse->save in memory->return newly cached response from memory 


 By default 100 items will be cached in memory for 24 hours. You may pass in your own instance of a Guava Cache to override the default policy.  


Busting through the cache
Alternatively you can call store.fresh(barcode) to get an Observable that skip the memory (and optional disk cache).


Fresh data call will look like: store.fresh()->fetch new networkResponse->save in memory->return newly cached response from memory 


Overnight background updates within our app us fresh to make sure that calls to store.get() will not have to hit network during normal usage.  Another good use case for fresh is pull to refresh.


Calls to both fresh() and get() emit 1 value and then call onCompleted or throw an error 


Stream
You may also call store.stream(barcode) which returns an Observable emitting the response for your barcode and then stays subscribed to receive any new items emitted for any calls to that store (for any barcode).  Think of stream as an Event Bus like feature that allow you to know when any new network hits happen for a particular store. If you can leverage the Rx operator filter() to only subscribe to a subset of emissions.


Inflight debouncer


There is an inflight debouncer as well, if same request is made within a minute of each other the same response will be returned (useful for when you’re app has many async calls to same data at startup or for when users are obsessively pulling to refresh).  As an example on start our app asynchronously calls ConfigStore.get() from 12 different places.  The first call blocks while all others wait for the data to arrive.  We have seen dramatic decrease in the data usage of our app since implementing the above in flight logic.


Adding a Parser


Since it is rare that data comes from the network in the format that your views need, Stores can delegate to a parser.   by using a ParsingStoreBuilder<T,V> rather than a StoreBuilder<T>.  ParsingStoreBuilder has an additional method parser() which can take a Parser<Raw, Parsed> 


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


Our updated data flow now looks like this store.get()->return memory cached version if exists->otherwise fetch new networkResponse->pass network response to parser->save parsed response in memory->return newly cached response from memory 


Middleware - GsonSourceParser
We also are releasing a separate middleware lib with parsers to help in cases where your fetcher is a Reader, BufferedSource or String and your parser is Gson: 
GsonReaderParser, 
GsonSourceParser, 
GsonStringParser.  


Our example can now be rewritten as:
Store<Article> Store = ParsingStoreBuilder.<BufferedSource, Article>builder()
                .nonObservableFetcher(this::getResponse)
       .parser(new GsonSourceParser<>(gson, Article.class))
       .open();


Disk Caching:


Stores can enable disk caching by passing in a Persister to the builder.  Whenever a new network request is made, it will first write to the disk cache and then read from the disk cache.


Now our data flow looks like:
store.get()->return memory cached version if exists otherwise get disk cached version if exists and cache in memory otherwise fetch new networkResponse write network response to disk read network response from disk cache pass disk response to parser (if parser exists) save parsed response in memory return newly cached response from memory. 


 Ideally, data will be streamed from network to disk using either a BufferedSource or Reader as your network raw type (rather than String).


       Store<String> Store = ParsingStoreBuilder.<BufferedSource, String>builder()
                              .nonObservableFetcher(this::ResponseAsSource)  //okhttp responseBody.source()
                .persister(new Persister<BufferedSource>() {
                    @Override
                    public Observable<BufferedSource> read(BarCode barCode) {
                      if(dataIsCached)
  return Observable.fromCallable(()->userImplementedCache.get(barcode));
Else
{
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
Store’s don’t care how you’re storing or retrieving your data from disk, as a result you can use stores with object storage or any database (realm, sql lite, couchDB,firebase etc).  The only requirement is that you can store and retrieve the data using the same type as your Fetcher.  Technically there is nothing stopping you from implementing an in memory cache for the “persister” implementation and instead have 2 levels of in memory caching (one with inflated and one with deflated models, allowing for sharing of the “persister” cache data between stores)


Note: When using a Parser and a disk cache, the parser will be called AFTER fetching from disk and not between the network and disk allow your persister to work on the network stream directly.


If using SqlLite we recommend working with SqlBrite.  If you are not using SqlBrite an Observable can be created rather simply with Observable.fromCallable(() -> getDBValue())




Middleware - SourcePersister & FileSystem


At NYTimes we find streaming network responses directly to disk to be the fastest form of persistence .  As a result we have included in the Middleware a blazing fast reactive FileSystem which depends on OKIO BufferedSources. We have also included a SourcePersister which will give you disk caching and works beautifully with GsonSourceParser


Store<String> Store = ParsingStoreBuilder.<BufferedSource, String>builder()
               .nonObservableFetcher(this::ResponseAsSource)  //okhttp responseBody.source()
               .persister(new SourcePersister(new FileSystemImpl(context.getFilesDir())))
   .parser(new GsonSourceParser<>(gson, String.class))
               .open();




The above builder is how we work with Data at New York Times.  With the above setup you have: 
memory caching with Guava Cache
Disk caching with FileSystem (you can reuse the same file system impl for all stores)
Parsing from a BufferedSource to a <T> (String in our case) with Gson
In flight request management
Ability to get cached data or bust through your caches (get vs fresh)


We recommend using the above setup of the builder for most Stores.  The SourcePersister implementation has a tiny memory footprint as it will stream bytes from network to disk and then from disk to parser. The streaming nature of our stores allows us to download 10 1mb+ json responses on app start without worrying about OOM on low memory devices.  As mentioned above, Stores allow us to do things like calling configStore.get() a dozen times asynchronously before our Main Activity finishes loading without blocking the main thread or flooding our network.




Factory Classes:


Besides using builders, stores can be constructed through factories:
Store<String> simpleStore = new RealStore<>(StoreFactory.of(fetcher, persister));
Store<String> simpleStore = new RealStore<>(StoreFactory.withParser(fetcher, persister,parser));


Subclassing a Store


We can also subclass a Store implementation (RealStore<T>) passing a factory method to the parent
public class SampleStore extends RealStore<String> {
   public SampleStore(Fetcher<String> f, Persister<String> p) {
       super(f, p);
   }
}
Or with a parser:
public class SampleStore extends RealStore<String> {
   public SampleStore(Fetcher<BufferedSource> fetcher,
                      Persister<BufferedSource> persister,
                      Parser<BufferedSource,String> parser) {
       super(fetcher, persister, parser);
   }
}


Subclassing is useful for when you’d like to inject Store dependencies or add a few helper methods to a store ie: 
public class SampleStore extends RealStore<String> {
   @Inject
   public SampleStore(Fetcher<String> f, Persister<String> p) {
       super(StoreFactory.of(f, p));
   }
}




Artifacts:
Since our stores depend heavily on Guava for Caching we have included 2 seperate artifacts:
Store-Base contains only Store classes and will need you to add RxJava + Guava to your code base.  We strongly recommend proguard to strip out unused methods  as guava has  a large method count (insert how many methods).  Store base is only 500 total methods.


Store-All  Contains Store and guava shaded dependency (V19 currently). We have proguarded out all parts of guava that we are not using which takes the method count down to under 1000 guava methods.  You will still need to add RxJava as a dependency to your app


Store-Middleware
Contains common implementation of Parser and Persister, ideally you would use the GsonSourceParser with the SourcePersister:


GsonReaderParser
GsonSourceParser
GsonStringParser
SourceFileReader
SourceFileWriter
SourcePersister
Middleware has a transitive dependency on GSON & OKIO




We’ve also include sample projects of how to use stores with:
OKHTTP + our FileSystem + GSON
Retrofit + FileSystem 
Retrofit + SqlBrite + SqlDelight


