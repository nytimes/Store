Change Log
==========


Version 1.0.6 *(2017-01-23/)*
----------------------------
* Bug Fix: fix race condition of 2 fetch requests too quickly (#74)
* Bug Fix: Expose MultiParser as Public
* API Change: Clarifying Store.stream (#73)
    1 Added the stream() method
    2 Deprecated old stream(barcode) and added migration helping in the documentation


Version 1.0.5 *DOES NOT EXIST* (We screwed up deployment)

Version 1.0.4 *(2017-01-19/)*
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


