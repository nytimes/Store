[![Build Status](https://travis-ci.org/NYTimes/Store.svg?branch=master)](https://travis-ci.org/NYTimes/Store)


# Store

Store — это легкая в использовании библиотека для реактивной загрузки данных под Android.

### Проблемы:

+ Современные Android-приложения нуждаются в удобном и всегда доступном представлении данных.
+ Пользователи ожидают, что загрузка данных не будет мешать их взаимодействию с приложением.
И от социальных, и от новостных, и от business-to-business приложений, пользователи ожидают бесшовного взаимодействия как при подключении к сети, так и в автономном режиме.
+ В международном роуминге большой объем загружаемых данных может привести к астрономическим счетам за связь.

Store представляет собой класс, который упрощает загрузку, парсинг, хранение и извлечение данных в вашем приложении.
Store похож на паттерн «репозиторий» [[https://msdn.microsoft.com/en-us/library/ff649690.aspx](https://msdn.microsoft.com/en-us/library/ff649690.aspx)], в дополнение предоставляя реактивный API, реализованный с помощью RxJava, который придерживается однонаправленного потока данных.

Store обеспечивает уровень абстракции между элементами UI и операциями с данными.

### Обзор

Store отвечает за управление загрузкой конкретного запроса данных.
Когда вы создаёте новую реализацию Store, вы предоставляете ему `Fetcher` — функцию, которая определяет, как будет происходить загрузка данных из сети.
Также вы можете настроить, как Store будет кэшировать данные в памяти и на диске, а так же то, как будет происходить их парсинг.
Store возвращает данные в виде `Observable`, обеспечивая удобную работу с потоками.
Созданный Store управляет логикой обработки потока данных, позволяя элементам пользовательского интерфейса использовать наиболее подходящий их источник, и гарантирует, что новейшие данные будут доступны для последующего использования в автономном режиме.
Store может использовать классы для промежуточной обработки данных (парсинг и кэширование), идущие в комплекте, или же использовать ваши собственные реализации.

Store использует RxJava и «склеивание» множественных запросов, чтобы минимизировать количество обращений к источнику данных в сети и кэшу на диске.
Используя Store, с помощью двух уровней кэширования (память и диск), вы исключите ситуации, когда один и тот же сетевой запрос будет выполняться избыточное количество раз.

### Полностью сконфигурированный Store
Для начала рассмотрим, как выглядит полностью сконфигурированный Store.
Затем последуют более простые примеры, демонстрирующие каждую деталь в отдельности.
```java
Store<ArticleAsset, Integer> articleStore = StoreBuilder.<Integer, BufferedSource, ArticleAsset>parsedWithKey()
                .fetcher(articleId -> api.getArticleAsBufferedSource(articleId))  //OkHttp responseBody.source()
                .persister(FileSystemPersister.create(FileSystemFactory.create(context.getFilesDir()),pathResolver))
                .parser(GsonParserFactory.createSourceParser(gson, ArticleAsset.Article.class))
                .open();
	      
```

Используя указанную выше конфигурацию вы получите:
+ Кэш в памяти, использующийся при смене конфигурации (например, при повороте экрана) 
+ Кэш на диске для использования в автономном режиме
+ Парсинг через потоковый API, чтобы ограничить использование памяти
+ Многофункциональный API для запроса данных: получение кэшированных/новых данных или подписка на их будущие обновления.

А теперь более подробно:

### Создание Store

Для создания Store используется паттерн builder (строитель).
Единственный обязательный параметр — это `.Fetcher<ReturnType,KeyType>`, который содержит единственный метод `fetch(key)`, возвращающий `Observable<ReturnType>`. 


``` java
Store<ArticleAsset, Integer> store = StoreBuilder.<ArticleAsset,Integer>key()
        .fetcher(articleId -> api.getArticle(articleId))  //OkHttp responseBody.source()
        .open();
```
Store использует типизированные ключи в качестве идентификаторов для данных.
Ключом может быть любой объект-значение (value object), который должным образом реализует методы `toString()`, `equals()` и `hashCode()`.
При вызове вашей `Fetcher`-функции, ей будет передан конкретный экземпляр ключа.
Аналогичным образом, ключ будет использоваться в качестве основного идентификатора в кэше (убедитесь, что ваш ключ правильно реализует `hashCode()`!) 

### Наша реализация ключа — BarCode

Для удобства мы включили в библиотеку нашу собственную реализацию ключа, называемую BarCode (штрихкод).
Barcode имеет два поля: ключ `String key` и тип `String type`.
``` java
BarCode barcode = new BarCode("Article", "42");
```
При использовании BarCode в качестве ключа, удобно использовать соответсвующий метод StoreBuilder:
``` java
Store<ArticleAsset, Integer> store = StoreBuilder.<ArticleAsset>barcode()
                .fetcher(articleBarcode -> api.getAsset(articleBarcode.getKey(),articleBarcode.getType()))
                .open();
```



### Публичный интерфейс: Get, Fetch, Stream, GetRefreshing

```java
Observable<Article> article = store.get(barCode);
```

В первый раз, когда вы подпишитесь на `store.get(barCode)`, ответ будет сохранён в кэше в памяти.
Все последующие вызовы `store.get(barCode)` с тем же ключом будут возвращать кэшированную версию данных, минимизируя ненужные запросы.
Это позволяет предотвратить загрузку свежих данных из сети (или другого внешнего источника), чтобы уменьшить расход трафика и экономить заряд батареи.
Хороший пример использования: пересоздание пользовательского интерфейса (activity или fragment) после поворота устройства — будут загружены кэшированные данные из Store.
Использование Store поможет вам избежать необходимости реализовывать эту логику на уровне представления.


Поток данных, которым управляет Store, на данный момент будет выглядеть следующим образом:
![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-1.jpg)


По умолчанию, 100 элементов будут сохранены в памяти на 24 часа.
Вы можете передать свой экземпляр Guava Cache, чтобы переопределить стандартную политику.


### Запрос свежих данных

В качестве альтернативы, вы можете вызвать `store.fetch(barCode)`, чтобы получить `Observable`, игнорируя кэш в памяти (и кэш на диске, если он используется).


Получение свежих данных выглядит следующим образом: `store.fetch()`
![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-2.jpg)


В приложении The New York Times фоновое обновление, выполняемое ночью, использует `fetch()`, чтобы во время обычного использования приложения вызов `store.get()` не приводил к сетевым запросам.
Другой хороший пример использования `fetch()` — обработка жеста pull-to-refresh, когда пользователь сам запрашивает обновление данных.


Методы `fetch()` и `get()` порождают одно значение и затем вызывают `onCompleted()` или выбрасывают исключение в случае ошибки.


### Поток
Для обновлений в реальном времени вы также можете вызвать метод `store.stream()`, который возвращает `Observable`, который порождает событие при сохранении нового объекта в Store.
Вы можете рассматривать этот поток как шину событий (Event Bus), позволяющую узнавать, когда происходят новые удачные обращения к сети для определённого Store.
Вы можете использовать оператор RxJava `filter()`, чтобы подписаться только на определенные события.

### Получение обновляющихся данных
Существует еще один специальный способ подписаться на Store: `getRefreshing(key)`.
`getRefreshing()` подпишется на  `get()`, который возвращает один результат, но в отличие от `get()`, `getRefreshing()` останется подписанным.
Каждый раз после вызова `store.clear(key)` все подписчики `getRefreshing(key)` подпишутся заново и принудительно создадут новый сетевой запрос данных.


### Оптимизация запросов

Store предлагает дополнительные механизмы для предотвращения дублирующихся запросов одних и тех же данных.
Если некий запрос выполняется в течение минуты после предыдущего точно такого же запроса, возвращается тот же ответ.
Это полезно в ситуациях, когда вашему приложению во время запуска требуется сделать много асинхронных вызовов для одних и тех же данных или когда пользователь интенсивно запрашивает обновление данных.
Например, новостное приложение The New York Times при запуске вызывает `ConfigStore.get()` из 12 разных мест.
Первый из запросов выполняется, а остальные ожидают поступления данных.
Мы увидели значительное сокращение объема использованных данных приложением после реализации этой логики.


### Добавление парсера

Поскольку данные из сети редко поступают в нужном формате, Store может делегировать их обработку парсеру с помощью `StoreBuilder.<BarCode, BufferedSource, Article>parsedWithKey()`

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

Теперь обновленный поток данных будет выглядеть следующим образом:

`store.get()` -> ![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-3.jpg)



### Промежуточная обработка данных — GsonSourceParser

Отдельный артефакт предоставляет парсеры, использующие Gson, которые могут быть полезны, если `Fethcer` возвращает Reader, BufferedSource или String:
- GsonReaderParser
- GsonSourceParser
- GsonStringParser

Они доступны через класс фабрики (GsonParserFactory).

Наш пример можно переписать так:
```java
Store<Article,Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
        .fetcher(articleId -> api.getArticle(articleId)) 
                .parser(GsonParserFactory.createSourceParser(gson, Article.class))
                .open();
```

В некоторых случаях вам может потребоваться проанализировать JSONArray верхнего уровня, в этом случае вы можете передать TypeToken.
```java
Store<List<Article>,Integer> store = StoreBuilder.<Integer, BufferedSource, List<Article>>parsedWithKey()
        .fetcher(articleId -> api.getArticles()) 
                .parser(GsonParserFactory.createSourceParser(gson, new TypeToken<List<Article>>() {}))
                .open();
		
		
```

Также существуют артефакты парсеров для Moshi и Jackson!


### Кэширование на диске

Можно включить кэширование данных на диске, используя экземпляр класса `Persister` при создании Store.
Всякий раз после выполнения сетевого запроса Store будет сохранять данные на диск, а затем считывать их.


Поток данных будет выглядеть так:
`store.get()` -> ![Simple Store Flow](https://github.com/nytm/Store/blob/feature/rx2/Images/store-5.jpg)

Идеальным вариантом будет, если данные будут передаваться из сети на диск с использованием BufferedSource или Reader в качестве типа данных (а не String).

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

Stores не заботятся о том, как вы сохраняете или извлекаете данные с диска.
В результате вы можете использовать Store с хранилищем объектов или любой базой данных (Realm, SQLite, CouchDB, Firebase etc).
Единственное требование заключается в том, что данные должны быть одного и того же типа при сохранении/получении, что и возвращаемое функцией Fetcher значение.
Теоретически, ничто не мешает вам реализовать свою версию класса `Persister`, который будет использовать  для кэширования данных только память устройства.
В таком случае в памяти будут храниться не один а два уровня кэша: первый с оригинальными данными, а второй с распарсенными, что позволит делиться кэшем `Persister` между разными экземплярами Store


**Примечание**: При использовании парсера и кэша на диске, парсер будет вызван ПОСЛЕ получения данных с диска, а не между успешным сетевым вызовом и сохранением на диск. 
Это позволяет классу `Persister` работать непосредственно с сетевым потоком.


При использовании SQLite мы рекомендуем библиотеку SqlBrite.
Если вы не используете SqlBrite, вы можете создать `Observable` с помощью `Observable.fromCallable(() -> getDBValue())`

### Промежуточный слой — SourcePersister и FileSystem

Мы установили, что наибольшей скорости сохранения можно добиться, выполняя потоковое сохранение данных из сети.
Поэтому мы включили отдельную библиотеку, предлагающую реактивную файловую систему, использующую `BufferedSource` из библиотеки Okio.
Также артефакт этот включает в себя `FileSystemPersister`, реализующий дисковый кэш для Store и прекрасно работающий с `GsonSourceParser`.
При использовании `FileSystemPersister` нужно передать ему реализацию `PathResolver`, определяющую пути к элементам кэша в файловой системе. 

Вернемся к первому примеру:

```java
Store<Article,Integer> store = StoreBuilder.<Integer, BufferedSource, Article>parsedWithKey()
               .fetcher(articleId -> api.getArticles(articleId)) 
               .persister(FileSystemPersister.create(FileSystemFactory.create(context.getFilesDir()),pathResolver))
               .parser(GsonParserFactory.createSourceParser(gson, String.class))
               .open();
```

Как уже упоминалось, это то, как мы работаем с сетевыми операциями в The New York Times.
Используя конфигурацию, указанную выше, вы получаете:
+ Кэширование в памяти с помощью Guava Cache
+ Кэширование на диске с помощью FileSystem (вы можете переиспользовать одну и ту же файловую систему для всех Store)
+ Парсинг данных из `BufferedSource` в `<T>` (Класс Article в нашем примере) с помошью Gson
+ Оптимизация запросов
+ Возможность использовать кэшированные данные или запросить свежие (методы `get()` и `fresh()`)
+ Возможность подписаться на все новые элементы, полученные из сети (метод `stream()`)
+ Возможность получить уведомление об очистке кэша, а также переподписаться после этого (метод `getRefreshing()`). Полезно, если нужно выполнить запрос типа POST, после чего другой обновить экран)

Мы рекомендуем использовать эту конфигурацию для большинства хранилищ Store.
Благодаря передаче данных из сети на диск и с диска в парсер в формате потока байтов, `SourcePersister` потребляет малое количество памяти.
Это позволяет нам загружать десятки ответов в формате json размером более 1mb  и не беспокоиться об исключениях OutOfMemory на устройствах с малым объемом памяти.
Как упоминалось выше, использование Store позволяет нам делать такие вещи, как вызов `configStore.get()` множествно раз асинхронно, прежде чем наша MainActivity закончит загрузку, не блокируя основной поток и не загружая сеть.

### RecordProvider
Если вы хотите, чтобы ваш Store знал об устаревании данных на диске, ваш `Persister` должер реализовывать интерфейс `RecordProvider`.
После этого, вы можете настроить работу Store одним из двух способов:

```java
store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .refreshOnStale()
                .open();
		
```


`refreshOnStale` инвалидирует дисковый кэш, каждый раз когда данные устареют.
Пользователь получит устаревшие данные.

Или же:

```java
        store = StoreBuilder.<BufferedSource>barcode()
                .fetcher(fetcher)
                .persister(persister)
                .networkBeforeStale()
                .open();
```

`networkBeforeStale` — Store попытается использовать сетевой источник, если данные устарели.
Если сетевой источник данных выбрасывает исключение или возвращает пустой ответ, Store будет использовать устаревшие данные.


### Создание подклассов Store

Можно отнаследоваться от класса реализации Store (`RealStore<T>`):

```java
public class SampleStore extends RealStore<String, BarCode> {
    public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}
```

Это может быть полезно, если вы хотите внедрить зависимость Store или добавить несколько вспомогательных методов:

```java
public class SampleStore extends RealStore<String, BarCode> {
   @Inject
   public SampleStore(Fetcher<String, BarCode> fetcher, Persister<String, BarCode> persister) {
        super(fetcher, persister);
    }
}
```


### Артефакты
Примечание: релизы синхронизированы с состоянием ветки master (а не develop).

+ **Cache** Кэш, извлеченный из библиотеки Guava (чтобы сократить количество методов)

	```groovy
	implementation 'com.nytimes.android:cache:CurrentVersion'
	```
+ **Store** Содержит только классы Store, зависит от RxJava и артефакта кэша  

	```groovy
	implementation 'com.nytimes.android:store:CurrentVersion'
	```
+ **Middleware** Парсеры Gson (не стесняйтесь создавать новые и предлагать PR)

    ```groovy
    implementation 'com.nytimes.android:middleware:CurrentVersion'
    ```
+ **Middleware-Jackson** Парсеры Jackson (не стесняйтесь создавать новые и предлагать PR)

    ```groovy
    implementation 'com.nytimes.android:middleware-jackson:CurrentVersion'
    ```
+ **Middleware-Moshi** Парсеры Moshi (не стесняйтесь создавать новые и предлагать PR)

    ```groovy
    implementation 'com.nytimes.android:middleware-moshi:CurrentVersion'
    ```
+ **File System** библиотека, использующая Okio Source/Sink + Middleware для сохранения потока данных из сети в файловую систему 

	```groovy
	implementation 'com.nytimes.android:filesystem:CurrentVersion'
	```


### Пример проекта

Директория app содержит тестовое приложение, демонстрирующее использование Store.
Кроме того, вики этого репозитория содержит некоторые рецепты для распространенных сценариев использования:
+ Простой пример: Retrofit + Store
+ Сложный пример: BufferedSource и Retrofit (или OkHTTP) + кэш на диске с FileSystem + GsonSourceParser
