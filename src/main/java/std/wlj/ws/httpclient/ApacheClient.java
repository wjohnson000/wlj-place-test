/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ws.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Implement basic GET, POST, PUT and DELETE functionality using Apache's "HttpClient" libraries.  This version doesn't
 * use the pooled connections, so a new connection is created for every request.  Each "CloseableHttpClient" must be
 * closed after use, so it's created as part of a "try-with-resources'.
 * 
 * @author wjohnson000
 *
 */
public class ApacheClient {

    /**
     * Create a client with a default contentType of "application/json" that will be used on all requests unless
     * overridden on a per-request basis.
     * 
     * @return
     */
    public static ApacheClient getDefault() {
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
    public static ApacheClient getDefault(String contentType, Map<String, String> headers) {
        ApacheClient client = new ApacheClient();
        client.contentType = contentType;
        client.headers = headers;
        return client;
    }

    String contentType;
    Map<String, String> headers;

    /** private constructor */
    private ApacheClient() { }

    /**
     * Submit a "GET" request to retrieve a resource
     * 
     * @param url URL to hit
     * @return result of the HTTP request, including status code, body, headers, exception
     */
    public WebResponse doGet(String url) {
        return doGet(url, contentType, headers);
    }

    public WebResponse doGet(String url, String contentType, Map<String, String> headers) {
        // Do a GET and parse the results
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader(HttpHeaders.ACCEPT, contentType);
            if (headers != null) {
                headers.entrySet().forEach(hdr -> httpGet.addHeader(hdr.getKey(), hdr.getValue()));
            }

            try (CloseableHttpResponse response = client.execute(httpGet);
                    InputStream ios = response.getEntity().getContent()) {
                return createWebResponse(response, ios);
            }
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * Submit a "POST" request to create a resource
     * 
     * @param url URL to hit
     * @return result of the HTTP request, including status code, body, headers, exception
     */
    public WebResponse doPost(String url, String body) {
        return doPost(url, body, contentType, headers);
    }

    public WebResponse doPost(String url, String body, String contentType, Map<String, String> headers) {
        // POST the request, return the "LOCATION" header
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HttpHeaders.ACCEPT, contentType);
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            StringEntity entity = new StringEntity(body, "UTF-8");
            httpPost.setEntity(entity);
            if (headers != null) {
                headers.entrySet().forEach(hdr -> httpPost.addHeader(hdr.getKey(), hdr.getValue()));
            }

            try (CloseableHttpResponse response = client.execute(httpPost);
                    InputStream ios = response.getEntity().getContent()) {
                return createWebResponse(response, ios);
            }
        } catch (IOException ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * Submit a "PUT" request to  update a resource
     * 
     * @param url URL to hit
     * @return result of the HTTP request, including status code, body, headers, exception
     */
    public WebResponse doPut(String url, String body) {
        return doPut(url, body, contentType, headers);
    }

    public WebResponse doPut(String url, String body, String contentType, Map<String, String> headers) {
        // PUT the request, but don't show any concern about the response
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader(HttpHeaders.ACCEPT, contentType);
            httpPut.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            StringEntity entity = new StringEntity(body, "UTF-8");
            httpPut.setEntity(entity);
            if (headers != null) {
                headers.entrySet().forEach(hdr -> httpPut.addHeader(hdr.getKey(), hdr.getValue()));
            }

            try (CloseableHttpResponse response = client.execute(httpPut);
                     InputStream ios = response.getEntity().getContent()) {
                return createWebResponse(response, ios);
            }
        } catch (IOException ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * Submit a "DELETE" request to delete a resource
     * 
     * @param url URL to hit
     * @return result of the HTTP request, including status code, body, headers, exception
     */
    public WebResponse doDelete(String url) throws Exception {
        return doDelete(url, headers);
    }

    public WebResponse doDelete(String url, Map<String, String> headers) throws Exception {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(url);
            if (headers != null) {
                headers.entrySet().forEach(hdr -> httpDelete.addHeader(hdr.getKey(), hdr.getValue()));
            }
            
            try (CloseableHttpResponse response = client.execute(httpDelete)) {
                return createWebResponse(response, null);
            }
        } catch (IOException ex) {
            return new WebResponse(0, null, null, ex);
        }

    }

    /**
     * Generate a reasonable {@link WebResponse} based on the result of the HTTP request
     * 
     * @param response response from the web request
     * @param ios input stream from which to fetch the response body, or null if no body is expected
     * @return
     * @throws IOException
     */
    protected WebResponse createWebResponse(CloseableHttpResponse response, InputStream ios) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();

        String body = null;
        if (ios != null) {
            body = IOUtils.toString(ios, StandardCharsets.UTF_8);
            EntityUtils.consumeQuietly(response.getEntity());
        }

        Map<String, String> headers = Arrays.stream(response.getAllHeaders())
              .collect(Collectors.toMap(hh -> hh.getName(), hh -> hh.getValue(), (v1, v2) -> v1 + ", " + v2));

        return new WebResponse(statusCode, body, headers);
    }

}
