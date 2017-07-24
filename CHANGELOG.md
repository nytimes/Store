Change Log
==========

Version 2.1.1 *(2017-07-24)*
----------------------------

**New Features**

* (#207) Add readAll / clearAll operations for a particular BarCode type
* (#238) Allow refreshable resource subscribers to stay subscribed after error

**Bug Fixes and Stability Improvements**

* (#234) Fix NoOp no connectivity network before stale behavior
* (#237) Fix NoClassDefFoundError for StandardCharsets GsonBufferedSourceAdapter
* (#245) Remove intermediate streams
* (#245) Update to Moshi 1.5.0
* (#233, #251) README updates

Version 2.1.0 *(2017-05-23)*
----------------------------

**New Features**

* (#199) expireAfterAccess added to MemoryPolicy
* (#211) Deprecate setExpireAfter and getExpireAfter -- use new expireAfterWrite or expireAfterAccess, see #199 for 
MemoryPolicy changes
* (#188) Add lambdas to Store and Filesystem modules
* (#198) Add Raw to BufferedSource transformer
* (#217) Add community projects section to README

**Bug Fixes and Stability Improvements**

* (#182) Fix networkBeforeStale on cold start with no connectivity
* (#200) Add a missing source.close() call
* (#205) Add Retrolambda plugin to all modules
* (#218) Update to RxJava 1.3.0 and use stable create(Action)

Version 2.0.4 *(2017-04-12)*
----------------------------

**Bug Fixes and Stability Improvements**

* (#178) Standardize store.stream() to emit only new items
* (#177) Fix typos
* (#176) Close source after write to filesystem
* (#166) Remove apt dependency and use annotationProcessor instead


Version 2.0.3 *(2017-03-23)*
----------------------------

**New Features**

* (#153) Translate Readme to Russian

**Bug Fixes and Stability Improvements**

* (#164) FileSystemPersister.persisterIsStale() should return false if record is missing or policy is unspecified
* (#166) Remove apt dependency and use annotationProcessor instead

Version 2.0.2 *(2017-03-13)*
----------------------------

**New Features**
* (#146) Remove dagger dependency and unused dependencies clean up

**Bug Fixes and Stability Improvements**
* (#144) clearMemory() (deprecated) now calls clear()
* (#149) fix sample app, store should be created within application class
* (#151) NoopParser should mimic Memcache settings
* (#154) Readme typos

Version 2.0.1 *(2017-02-28)*
----------------------------

**New Features**
* (#137) FileSystemRecordPersister
* (#138) Introduce RecordPersisterFactory
* (#140) Add FileSystemRecordPersisterFactory

**Bug Fixes and Stability Improvements**
* (#134) Do not depend on lint task if it does not exist.
* (#133) Remove StandardCharsets usage to work on API < 19
* (#136) add KeyParseFunc for times when parser needs input val
* (#141) fix clear one barcode

Version 2.0.0 *(2017-02-13)*
----------------------------

**Breaking Changes**  - See Wiki/Closed PRs for more detail

* (#122) Store v1 removal in favor of Stores with generic keys - Store<ReturnType> becomes Store<ReturnType,Key>
* (#110) rework builders - See StoreBuilder for changes
* (#86)  Migrate Barcode to any Type - No longer need to use Barcode as your request type!

**New Features**
* (#94)  use javax annotations instead of intellij
* (#117) Feature/clear all cache - Disk Caching if Persister implements Clearable
* (#115) Feature/filepersister - Using Store with FileSystem no longer requires our BarCode type
* (#120) Avoid multiple resolves of the same Key
* (#111) networkBeforeStale and refreshOnStale - 2 ways to control Persisters that are StaleAware
* (#103) GetRefreshing - Like get but will repeat when clear
* (#113) lets try this travis snapshot deploy again - Snapshot deployment now works woo!

**Bug Fixes and Stability Improvements**
* (#125) Fix name on occurences of Key
* (#124) Create single instance of empty BarCode
* (#123) Simplify Persister clear and RefreshSubject notify 
* (#114) remove extranous exception throw
* (#108) Update to Moshi 1.4.0
* (#106) remove espresso, unneeded, conflicts with javax.annotations
* (#101) fixes inflight caching errors
* (#99)  Remove dead code and add/remove empty lines were needed
* (#97)  clear needs to clear noop disk and inflight
* (#126) Remove duplicate element from versions array

Version 1.0.7 *(2017-01-30)*
----------------------------
* Feature: add additional create(FileSystem) within SourcePersister to allow clearing fileSystem cache (#77)
* Feature: add error prone analyzer (#79)
* Feature: convert store to java project from android (#85)
* Bug Fix: Removed application meta data (#80)
* Bug Fix: fixes race condition in memory cache (#90)
* Bug Fix: add back in flight 1 minute debouncing (was missing somehow?) (#92)
* Documentation Fix: Classnames up, field names down (#81)



Version 1.0.6 *(2017-01-23)*
----------------------------
* Bug Fix: fix race condition of 2 fetch requests too quickly (#74)
* Bug Fix: Expose MultiParser as Public
* API Change: Clarifying Store.stream (#73)
    1 Added the stream() method
    2 Deprecated old stream(barcode) and added migration helping in the documentation


Version 1.0.5 *DOES NOT EXIST* 
----------------------------
*(We screwed up deployment)

Version 1.0.4 *(2017-01-19)*
----------------------------
* Bug Fix: Fix memory caching issue with Equivalence.Equals (#70)

Version 1.0.3 *(2017-01-17)*
----------------------------
* New Feature: Multi Parser (#61)   - You can now pass in a list of Parsers and do multi Parsing steps
* New Feature: Jackson Middleware (#60)
* New Feature:  Moshi Middleware (#55)
* New Feature: change GsonParsers to work with a type rather than a class (#46)  - Can now parse Top Level Array
* Enhancement: Preconditions for middleware module (#56)
* Enhancement: checkstyle  (#48)
* Enhancement: pmd  (#50)
* Enhancement: Nullability Annotations (#52)
* Bug Fix: always call sink.close() from finally block (#63)
* Bug Fix: remove maven.em.nytimes.com from repositories block (#62)
* In Progress: Annotation Processor to Generate Stores for Retrofit Interfaces feature/annotations



Version 1.0.2 *(2017-01-10)*
----------------------------

* API CHANGE: SourcePersisterFactory & GsonParserFactory 

    https://github.com/NYTimes/Store/pull/35
* API CHANGE: FileSystemFactory 

    https://github.com/NYTimes/Store/pull/28
  
* Addition of Travis CI, currently runs tests only


