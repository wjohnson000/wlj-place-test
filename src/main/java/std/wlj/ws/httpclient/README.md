# HTTP Client : Options

There are a number of libraries that ease the complexities of doing HTTP calls.  The files in the package illustrate four different classes that implement basic GET, POST, PUT and DELETE operations.  The are broken down as follows:

* HTTP client classes (4)
* WebResponse class
* Test classes that exercise the functionality of each client

### Response Class

The "WebResponse" class is a simple POJO that manages four basic values that could be of interest to a client calling a HTTP method.  The class was created because no single value would suffice for every case:

* Return status (int)
* Response body (String)
* Response headers (Map) -- multiple values for a single header are concatenated and comma-separated
* Exception -- what went wrong

The client can call the "isOK()" method to determine if the call succeeded (i.e., no exception thrown) or not.


### HTTP Client Classes

There are four clients, each of which support GET, POST, PUT and DELETE operations.  The constructor is private, but each class supports two factory methods to get a client:

* no parameters -- the client is set up to communicate with "application/json" as the content-type and accept values.
* define a content-type and provide a list of headers that should be included with every HTTP call.

The four client classes are:

1. JavaClient -- this class uses no third-party frameworks: all communication is done via standard classes in the "java.net" package.  Based on some initial testing, the "Content-Type" on a DELETE method must be set to "application/x-www-form-urlencoded".  Thus the client overrides the "Accept" and "Content-Type" headers on a DELETE.  This client works just fine, but why use it when there are other readily-available frameworks?

2.  ApacheClient -- this class uses Apache's "HttpClient" framework, which is a well-tested, well-used and well-supported library.  The code is pretty straight-forward, and this class would meet our needs.  NOTE: when doing a modest "load" test -- 6700 GET operations, single-threaded -- this was by far the slowest client, taking about twice as long as the other three.

3.  ApacheClientPooled -- this class also uses Apache's "HttpClient" framework, like the "ApacheClient" class, but it uses a "PoolingHttpClientConnectorManager" to manage a pool of HTTP connections.  This significantly speeds up HTTP calls, making it as performant as the other two classes.  The biggest challenge here would be tuning the HttpConnection pool so that there are enough, but not too many, connections available.

4.  SpringWebClient -- this class uses Spring's new "WebClient" framework, which is the replacement for the older "RestTemplate" framework.  "RestTemplate" is still around, but it's pretty much closed to enhancements and bug fixes.  Although we could use it, the recommendation is to move to the new "WebClient", which is built on a "reactive" programming framework.  "WebClient" supports both synchronous and asynchronous calls; the other three are synchronous only.  In the current example implementation all calls are synchronous, but using "WebClient" allows for future enhancements that aren't supported in plain Java or HttpClient.


### Test Classes

Three test classes show how each of the clients work.

* TestClients -- this is a very simple class does a single GET against a single endpoint, displaying the return status, the headers, and the body.  It hits the "dev" system, but doesn't need a session ID to run.
* TestClientsCRUD -- this class also hits the "dev" system and does a GET, POST (create a new name), GET (read the newly-created name) and DELETE (delete the new name).  All four clients are exercised.  It also runs against the "dev" system and so required a valid session ID for the Authorization header.
* TestClientsLots -- this class hits the endpoint to get all name IDs in a certain collection (count=6712) and then reads each name once.  Most of the clients run in about 10 minutes, but the "ApacheClient" class takes almost 20 minutes.  NOTE: the code is single-threaded.  Additional tests would need to be done in a multi-threaded environment.


### Recommendations ...

Because of where Spring 5 and Java are going, it might be best to use the "SpringWebClient" for all internal HTTP calls, for example, used by "homelands-admin" for all calls to "homelands-core".

But whichever direction we choose, we need to clean up the code a bit, do some unit tests, and make sure it's sufficient for our needs.
