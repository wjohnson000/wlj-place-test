/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ws.httpclient;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.http.entity.ContentType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implement the basic GET, POST, PUT and DELETE methods using Springs "WebClient" classes.  These are reactive clients
 * that are intended to replace the existing "RestTemplate" classes.
 * <p/>
 * Although the library supports asynchronous calls, the basic functionality here blocks on all HTTP operations until
 * the call succeeds and results can be returned.
 * <p/>
 * NOTE: requires the use of the "org.springframework.boot/spring-boot-starter-webflux" dependency.
 * 
 * @author wjohnson000
 *
 */
public class SpringWebClient {

    /**
     * Create a client with a default contentType of "application/json" that will be used on all requests unless
     * overridden on a per-request basis.
     * 
     * @return
     */
    public static SpringWebClient getDefault() {
        return getDefault(ContentType.APPLICATION_JSON.getMimeType(), Collections.emptyMap());
    }

    /**
     * Create a client with a "contentType" that will be used as both the "content-type" and "accept" http
     * headers, and a map of additional header values to be used on all requests, unless overridden on a
     * per-request basis.
     * 
     * @param contentType content type
     * @param headers default headers for all calls
     * @return
     */
    public static SpringWebClient getDefault(String contentType, Map<String, String> headers) {
        SpringWebClient client = new SpringWebClient();
        client.contentType = contentType;
        client.headers = headers;
        return client;
    }

    String contentType;
    Map<String, String> headers;

    /** private constructor */
    private SpringWebClient() { }

    /**
     * Do a GET operation against the url ... NOTE: this method should not be used
     * if there are parameters that need to be url-encoded
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public WebResponse doGet(String url) {
        return doGet(url, contentType, headers);
    }

    public WebResponse doGet(String url, String contentType, Map<String, String> headers) {
        try {
            WebClient client = WebClient.create();

            ClientResponse response = client.get()
                                    .uri(url)
                                    .header("Content-Type", contentType)
                                    .header("Accept", contentType)
                                    .headers(addHeaders(headers))
                                    .exchange()
                                    .block();
            
            int status = response.statusCode().value();
            String body = response.toEntity(String.class).block().getBody();
            Map<String, String> respHeaders = response.headers().asHttpHeaders().entrySet().stream()
                                    .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));
            
            return new WebResponse(status, body, respHeaders);
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * Do a POST operation against the url ...
     * 
     * @param url url
     * @param model model object for the request body
     * @return new model object
     */
    public WebResponse doPost(String url, String body) {
        return doPost(url, body, contentType, headers);
    }

    public WebResponse doPost(String url, String body, String contentType, Map<String, String> headers) {
        try {
            WebClient client = WebClient.create();
            
            ClientResponse response = client.post()
                                    .uri(url)
                                    .bodyValue(body)
                                    .header("Content-Type", contentType)
                                    .header("Accept", contentType)
                                    .headers(addHeaders(headers))
                                    .exchange()
                                    .block();
            
            int status = response.statusCode().value();
            String respBody = response.toEntity(String.class).block().getBody();
            Map<String, String> respHeaders = response.headers().asHttpHeaders().entrySet().stream()
                                    .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));
            
            return new WebResponse(status, respBody, respHeaders);
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * Do a PUT operation against the url ...
     * 
     * @param url url
     * @param model model object for the request body
     * @return new model object
     */
    public WebResponse doPut(String url, String body) {
        return doPut(url, body, contentType, headers);
    }

    public WebResponse doPut(String url, String body, String contentType, Map<String, String> headers) {
        try {
            WebClient client = WebClient.create();
            
            ClientResponse response = client.put()
                                    .uri(url)
                                    .bodyValue(body)
                                    .header("Content-Type", contentType)
                                    .header("Accept", contentType)
                                    .headers(addHeaders(headers))
                                    .exchange()
                                    .block();
            
            int status = response.statusCode().value();
            String respBody = response.toEntity(String.class).block().getBody();
            Map<String, String> respHeaders = response.headers().asHttpHeaders().entrySet().stream()
                                    .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));
            
            return new WebResponse(status, respBody, respHeaders);
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }

    }

    /**
     * Do a DELETE operation against the url ...
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public WebResponse doDelete(String url) throws Exception {
        return doDelete(url, headers);
    }

    public WebResponse doDelete(String url, Map<String, String> headers) throws Exception {
        try {
            WebClient client = WebClient.create();
            
            ClientResponse response = client.delete()
                                    .uri(url)
                                    .headers(addHeaders(headers))
                                    .exchange()
                                    .block();
            
            int status = response.statusCode().value();
            Map<String, String> respHeaders = response.headers().asHttpHeaders().entrySet().stream()
                                    .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));
            
            return new WebResponse(status, null, respHeaders);
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * Return a {@link Consumer} that will add extra headers if they're not already defined.  If the list of
     * headers is null or empty, return a consumer that does nothing.
     * 
     * @param headers headers to add
     * @return
     */
    protected Consumer<HttpHeaders> addHeaders(Map<String, String> headers) {
        if (headers == null  ||  headers.isEmpty()) {
            return httpH -> { };
        } else {
            return httpH -> {
                headers.entrySet().forEach(hh -> {
                    if (! httpH.containsKey(hh.getKey())) {
                        httpH.add(hh.getKey(), hh.getValue());
                    }
                });
            };
        }
    }
}
