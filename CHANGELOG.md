Change Log
==========

The change log for Store version 1.x can be found [here](https://github.com/NYTimes/Store/blob/develop/CHANGELOG.md).

Version 3.0.1 *(2018-03-20)*
----------------------------

**Bug Fixes and Stability Improvements**

* (#311) Update Kotlin & AGP versions
* (#314) Fix issues occured from RxJava1 dependency

Version 3.0.0 *(2018-02-01)*
----------------------------

**New Features**

* (#275) Add ParsingFetcher that wraps Raw type Parser and Fetcher

**Bug Fixes and Stability Improvements**

* (#267) Kotlin 1.1.4 for store-kotlin 
* (#290) Remove @Experimental from store-kotlin API
* (#283) Update build tools to 26.0.2
* (#259, #261, #272, #289, #303) README + documentation updates
* (#310) Sample app fixes

Version 3.0.0-beta *(2017-07-26)*
----------------------------

**New Features**

* (#229) Add store-kotlin module
* (#254) Add readAll / clearAll operations for a particular BarCode type
* (#250) Return object with meta data
* Create Code of Conduct

**Bug Fixes and Stability Improvements**

* (#239) Fix NoClassDefFoundError for StandardCharsets GsonBufferedSourceAdapter
* (#243) Update README for Rx2
* (#247) Remove intermediate streams
* (#246) Update to Moshi 1.5.0
* (#252) Fix stream for a single barcode

Version 3.0.0-alpha *(2017-05-23)*
----------------------------

This is a first alpha release of Store ported to RxJava 2.

**New Features**

* (#155) Port to RxJava 2
* (#220) Packages have been renamed to store3 to allow use of this artifact alongside the original Store
* (#185) Return Single/Maybe where appropriate
* (#189) Add lambdas to Store and Filesystem modules
* (#214) expireAfterAccess added to MemoryPolicy
* (#214) Deprecate setExpireAfter and getExpireAfter -- use new expireAfterWrite or expireAfterAccess, see #199 for 
MemoryPolicy changes
* (#214) Add Raw to BufferedSource transformer


**Bug Fixes and Stability Improvements**

* (#214) Fix networkBeforeStale on cold start with no connectivity
* (#214) Add a missing source.close() call
* (#164) FileSystemPersister.persisterIsStale() should return false if record is missing or policy is unspecified
* (#166) Remove apt dependency and use annotationProcessor instead
* (#214) Standardize store.stream() to emit only new items
* (#214) Fix typos
* (#214) Close source after write to filesystem