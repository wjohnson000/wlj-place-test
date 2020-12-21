package std.wlj.ws.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Basic HTTP operations using only the "java.net" and related classes.  No third party stuff needs to be included.
 * <p/>
 * NOTE: for a delete operation, the mime type needs to be "application/x-www-form-urlencoded".  So that's hard-coded
 * in the method.
 * 
 * @author wjohnson000
 *
 */
public class JavaClient {

    /** Time-out values*/
    private static int readTimeOut = 120000;
    private static int connectTimeOut = 10000;

    public static JavaClient getDefault() {
        return getDefault("application/json", Collections.emptyMap());
    }

    public static JavaClient getDefault(String contentType, Map<String, String> headers) {
        JavaClient client = new JavaClient();
        client.contentType = contentType;
        client.headers = headers;
        return client;
    }

    String contentType;
    Map<String, String> headers;

    /** private constructor */
    private JavaClient() { }

    /**
     * Do a GET operation against the url ... NOTE: this method should not be used
     * if there are parameters that need to be url-encoded
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public WebResponse doGet(String url) throws Exception {
        return doGet(url, contentType, headers);
    }

    public WebResponse doGet(String url, String contentType, Map<String, String> headers) throws Exception {
        return doRequest(url, "GET", contentType, headers);
    }

    /**
     * Do a GET operation against the url ... after URL-encoding the parameters ...
     * NOTE: this method should be used when there are parameters that need to be
     * url-encoded
     * 
     * @param url url
     * @param keyVal list of key+value combinations
     * @return new model object, or whatever the service returns
     */
    public WebResponse doGet(String url, String contentType, Map<String, String> headers, String... keyVal) throws Exception {
        char ch = '?';
        StringBuilder buff = new StringBuilder();
        buff.append(url.toString());
        for (int i=0;  i<keyVal.length;  i+=2) {
            buff.append(ch).append(keyVal[i]).append("=").append(URLEncoder.encode(keyVal[i+1], "UTF-8"));
            ch = '&';
        }
        return doGet(buff.toString(), contentType, headers);
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
        Map<String, String> newHeaders = (headers == null) ? new HashMap<>() : new HashMap<>(headers);
        newHeaders.remove("Accept");
        newHeaders.put("Content-Type", "application/x-www-form-urlencoded");

        return doRequest(url, "DELETE", null, newHeaders);
    }

    /**
     * Do a HEAD operation against the url ...
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public WebResponse doHead(String url) throws Exception {
        return doHead(url, headers);
    }

    public WebResponse doHead(String url, Map<String, String> headers) throws Exception {
        return doRequest(url, "HEAD", null, headers);
    }

    /**
     * Do an OPTIONS operation against the url ...
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public WebResponse doOptions(String url) throws Exception {
        return doOptions(url, headers);
    }

    public WebResponse doOptions(String url, Map<String, String> headers) throws Exception {
        return doRequest(url, "OPTIONS", null, headers);
    }

    /**
     * Do a POST operation against the url ...
     * 
     * @param url url
     * @param model model object for the request body
     * @return new model object
     */
    public WebResponse doPost(String url, String model) throws Exception {
        return doPost(url, model, contentType, headers);
    }

    public WebResponse doPost(String url, String model, String contentType, Map<String, String> headers) throws Exception {
        return doRequestWithEntity(url, model, "POST", contentType, headers);
    }

    /**
     * Do a PUT operation against the url ...
     * 
     * @param url url
     * @param model model object for the request body
     * @return new model object
     */
    public WebResponse doPut(String url, String model) throws Exception {
        return doPut(url, model, contentType, headers);
    }

    public WebResponse doPut(String url, String model, String contentType, Map<String, String> headers) throws Exception {
        return doRequestWithEntity(url, model, "PUT", contentType, headers);
    }

    /**
     * The difference between a WebConstants.METHOD_GET, WebConstants.METHOD_DELETE, "HEAD" and a "OPTIONS" interface is minimal.
     * All of those actions come here to do the *real* work.
     * String
     * @param url URL to hit
     * @param method HTTP method, "GET", "DELETE", "HEAD" or "OPTIONS"
     * 
     * @return result of the operation
     * @throws Exception if something bad happens
     */
    public static WebResponse doRequest(String url, String method, String contentType, Map<String, String> headers) {
        try {
            URLConnection urlConn = setupConnection(url, contentType, headers);
            if (urlConn instanceof HttpURLConnection) {
                HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
                httpUrlConn.setRequestMethod(method);
                HttpURLConnection.setFollowRedirects(true);
                httpUrlConn.setDoOutput(true);

                httpUrlConn.connect();
                return handleResponse(httpUrlConn, contentType, headers);
            } else {
                return new WebResponse(0, null, null, new Exception("Weird error occurred ... unable to set up connection."));
            }
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * The difference between a WebConstants.METHOD_PUT and a WebConstants.METHOD_POST interface is minimal.  Either
     * action comes here to do the *real* work.
     * 
     * @param url URL to hit
     * @param model request contents, ready to be marshaled
     * @param method either WebConstants.METHOD_PUT or WebConstants.METHOD_POST
     * @return result of the operation
     * @throws Exception if something bad happens
     */
    private static WebResponse doRequestWithEntity(String url, String model, String method, String contentType, Map<String, String> headers) {
        try {
            URLConnection urlConn = setupConnection(url, contentType, headers);
            if (urlConn instanceof HttpURLConnection) {
                HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
                httpUrlConn.setDoOutput(true);
                httpUrlConn.setRequestMethod(method);
                
                OutputStreamWriter out = new OutputStreamWriter(httpUrlConn.getOutputStream());
                out.write(model);
                out.close();
                
                return handleResponse(httpUrlConn, contentType, headers);
            } else {
                return new WebResponse(0, null, null, new Exception("Weird error occurred ... unable to set up connection."));
            }
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }
    }

    /**
     * Set up the connection ... here we do a little cheating to determine if the URL
     * hasn't been [possibly hasn't been] correctly encoded yet.
     * 
     * @param url URL to be hit
     * @return
     * @throws Exception
     */
    private static URLConnection setupConnection(String url, String contentType, Map<String, String> headers) throws Exception {
        URL urlUrl = new URL(url);

        if (url.toString().contains(" ")) {
            URI uri = new URI(urlUrl.getProtocol(), urlUrl.getUserInfo(), urlUrl.getHost(), urlUrl.getPort(), urlUrl.getPath(), urlUrl.getQuery(), null);
            urlUrl = uri.toURL();
        }

        URLConnection urlConn = urlUrl.openConnection();
        urlConn.setConnectTimeout(connectTimeOut);
        urlConn.setReadTimeout(readTimeOut);
        if (! headers.containsKey("Content-Type")) {
            urlConn.setRequestProperty("Accept", contentType);
            urlConn.setRequestProperty("Content-Type", contentType);
        }
        headers.entrySet().forEach(kv -> urlConn.setRequestProperty(kv.getKey(), kv.getValue()));

        return urlConn;
    }

    /**
     * Handle the response of the HttpURLConnection.
     * 
     * @param httpUrlConn HttpURLConnection
     * 
     * @return Marshaled data, or null if no response body, or error
     * @throws Exception
     */
    private static WebResponse handleResponse(HttpURLConnection httpUrlConn, String contentType, Map<String, String> headers) {
        try {
            int status = httpUrlConn.getResponseCode();

            Map<String, String> respHeaders = httpUrlConn.getHeaderFields().entrySet().stream()
                                     .collect(Collectors.toMap(hdr -> hdr.getKey(),
                                                               hdr -> hdr.getValue().stream().collect(Collectors.joining(", "))));

            String respText = null;
            if (status == 200  ||  status == 201) {
                int len = 0;
                byte[] buffer = new byte[1024*16];
                ByteArrayOutputStream bais = new ByteArrayOutputStream();
                InputStream inStr = httpUrlConn.getInputStream();
                while (inStr != null  &&  (len = inStr.read(buffer)) > 0) {
                    bais.write(buffer, 0, len);
                }
                respText = bais.toString();
            } else if (status == 301  ||  status == 302  ||  status == 303 || status == 307) {
                String location = httpUrlConn.getHeaderField("Location");
                System.out.println("[RE-DIRECT]: " + location);
                return doRequest(location, "GET", contentType, headers);
            }
            
            return new WebResponse(status, respText, respHeaders);
        } catch(Exception ex) {
            return new WebResponse(0, null, null, ex);
        }
    }
}
