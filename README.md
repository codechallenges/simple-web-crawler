Simple Java Web Crawler
=====================

----------

Requirements
-------------

> - To create a simple crawler that should be limited to one domain. Given a starting URL – say example.com - it should visit all pages within the domain, but not follow the links to external sites such as Google or Twitter.

> - The output should be a simple site map, showing links to other pages under the same domain, links to static content such as images, and to external URLs.


----------


System Requirements
-------------

> - java 1.7 and above

> - jsoup jar (http://jsoup.org/packages/jsoup-1.8.3.jar)

> - Eclipse IDE (optional)

----------

Running from Eclipse
-------------

> - Checkout/clone project from GIT
> - Its a simple java project which can be imported into Eclipse simply by doing Import-> Existing Projects into Workspace. 
> - Once imported, it can be run from within Eclipse.


----------

Running from Command Line
-------------

Following are the instructions if using command line to compile and run:
> - cd into the directory: **/simple-web-crawler/src**
> - to compile: **javac -cp ../lib/jsoup-1.8.3.jar com/crawler/Crawler.java -d ../bin**
> - to run (windows): **java -cp ../lib/jsoup-1.8.3.jar;../bin com.crawler.Crawler**
> - to run (unix): **java -cp ../lib/jsoup-1.8.3.jar:../bin com.crawler.Crawler**
> - Pass arguments as required. Please see below for details.


----------


Params/Arguments
-------------
Following are the parameters which are supported in the program. They are passed as program arguments but none of them mandatory (as all of them have their default values in the program).
Sequence of passing these parameters does not matter. Program will determine the right switches and execute accordingly.

> - -BU (BASE_URL): URL to be crawled (STRING - defaults to http://www.example.com)
> - -CS (CRAWL_STATIC):  To crawl static resources or not (true|false: defaults to false)
> - -DB (DEBUG): To print DEBUG trace or not (true|false: defaults to false)
> - -MD (MAX_DEPTH): Maximum depth to be crawled, to prevent it from running till infinity (value should be a positive integer; defaults to '3')
> - -SL (SLEEP_TIME): Sleep interval, because some sites prohibit too many near simultaneous requests and throw HTTP ERROR [429] (value should be a positive integer; defaults to '0')


Example (windows): `'java -cp ../lib/jsoup-1.8.3.jar;../bin com.crawler.Crawler -BUhttp://www.yahoo.com -CStrue -DBtrue -MD3 -ST300`


Example (unix): `'java -cp ../lib/jsoup-1.8.3.jar:../bin com.crawler.Crawler -BUhttp://www.yahoo.com -CStrue -DBtrue -MD3 -ST300`

----------



Credits
-------------

> - http://jsoup.org/


----------

Support
-------------
Email at: firozkhan@outlook.com

----------
