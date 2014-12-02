package std.wlj.ws.rawhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.familysearch.standards.place.ws.model.RootModel;


public class TestUtil {

    /** JSON object mapper */
    public static final ObjectMapper jsonMapper = new ObjectMapper();
    public static final ObjectReader jsonReader = jsonMapper.reader(RootModel.class);
    public static final ObjectWriter jsonWriter = jsonMapper.writerWithType(RootModel.class);

    /** Time-out values*/
    private static int readTimeOut = 120000;
    private static int connectTimeOut = 10000;

    /** List of bad URLs */
    private static List<String> badUrls = new ArrayList<String>();


    /**
     * Do a GET operation against the url ... NOTE: this method should not be used
     * if there are parameters that need to be url-encoded
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public static RootModel doGET(URL url) throws Exception {
        return doRequest(url, "GET");
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
    public static RootModel doGET(URL url, String... keyVal) throws Exception {
        char ch = '?';
        StringBuilder buff = new StringBuilder();
        buff.append(url.toString());
        for (int i=0;  i<keyVal.length;  i+=2) {
            buff.append(ch).append(keyVal[i]).append("=").append(URLEncoder.encode(keyVal[i+1], "UTF-8"));
            ch = '&';
        }
        return doGET(new URL(buff.toString()));
    }

    /**
     * Do a DELETE operation against the url ...
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public static RootModel doDELETE(URL url) throws Exception {
        return doRequest(url, "DELETE");
    }

    /**
     * Do a HEAD operation against the url ...
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public static RootModel doHEAD(URL url) throws Exception {
        return doRequest(url, "HEAD");
    }

    /**
     * Do an OPTIONS operation against the url ...
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public static RootModel doOPTIONS(URL url) throws Exception {
        return doRequest(url, "OPTIONS");
    }

    /**
     * Do a POST operation against the url ...
     * 
     * @param url url
     * @param model model object for the request body
     * @return new model object
     */
    public static RootModel doPOST(URL url, RootModel model) throws Exception {
        return doRequestWithEntity(url, model, "POST");
    }


    /**
     * Do a PUT operation against the url ...
     * 
     * @param url url
     * @param model model object for the request body
     * @return new model object
     */
    public static RootModel doPUT(URL url, RootModel model) throws Exception {
        return doRequestWithEntity(url, model, "PUT");
    }

    /**
     * Wipe clean the list of bad URLs
     */
    public static void resetBadUrl() {
        badUrls.clear();
    }

    /**
     * Retrieve the list of bad URLs
     * @return
     */
    public static List<String> getBadUrls() {
        return badUrls;
    }

    /**
     * The difference between a WebConstants.METHOD_GET, WebConstants.METHOD_DELETE, "HEAD" and a "OPTIONS" interface is minimal.
     * All of those actions come here to do the *real* work.
     * 
     * @param url URL to hit
     * @param method HTTP method, WebConstants.METHOD_GET, WebConstants.METHOD_DELETE, "HEAD" or "OPTIONS"
     * 
     * @return result of the operation
     * @throws Exception if something bad happens
     */
    public static RootModel doRequest(URL url, String method) throws Exception {
        URLConnection urlConn = setupConnection(url);
        System.out.println(method + " --> " + urlConn.getURL());

        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
            httpUrlConn.setRequestMethod(method);
            httpUrlConn.setConnectTimeout(300000);

            httpUrlConn.connect();
            return handleResponse(httpUrlConn);
        }

        return null;
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
    private static RootModel doRequestWithEntity(URL url, RootModel model, String method) throws Exception {
        URLConnection urlConn = setupConnection(url);
        System.out.println(method + " --> " + urlConn.getURL());

        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setRequestProperty("Content-Type", "application/standards-places-v2+json");
            httpUrlConn.setRequestMethod(method);

            OutputStreamWriter out = new OutputStreamWriter(httpUrlConn.getOutputStream());
            out.write(toJSON(model));
            out.close();

            return handleResponse(httpUrlConn);
        }

        return null;
    }

    /**
     * Set up the connection ... here we do a little cheating to determine if the URL
     * hasn't been [possibly hasn't been] correctly encoded yet.
     * 
     * @param url URL to be hit
     * @return
     * @throws Exception
     */
    private static URLConnection setupConnection(URL url) throws Exception {
        URLConnection urlConn = null;
        if (url.toString().contains(" ")) {
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
            URL newUrl = uri.toURL();
            urlConn = newUrl.openConnection();
        } else {
            urlConn = url.openConnection();
        }

        urlConn.setConnectTimeout(connectTimeOut);
        urlConn.setReadTimeout(readTimeOut);
        urlConn.setRequestProperty("Accept-Language", "en");
        urlConn.setRequestProperty("Accept-Charset", "utf-8");
        urlConn.setRequestProperty("Accept", RootModel.APPLICATION_JSON_PLACES);
//        urlConn.setRequestProperty("Accept", RootModel.APPLICATION_XML_PLACES);

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
    private static RootModel handleResponse(HttpURLConnection httpUrlConn) throws Exception {
        int status = httpUrlConn.getResponseCode();
        System.out.println("RC: " + status);
        System.out.println("MSG: " + httpUrlConn.getResponseMessage());
        System.out.println("HDR: " + httpUrlConn.getHeaderFields());

        if (status == 200  ||  status == 201) {
            int len = 0;
            byte[] buffer = new byte[1024*16];
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            InputStream inStr = httpUrlConn.getInputStream();
            while ((len = inStr.read(buffer)) > 0) {
                bais.write(buffer, 0, len);
            }

            String respText = bais.toString();
            return fromJSON(respText);
        } else {
            if (httpUrlConn.getErrorStream() != null) {
                int len = 0;
                byte[] buffer = new byte[1024*16];
                ByteArrayOutputStream bais = new ByteArrayOutputStream();
                InputStream inStr = httpUrlConn.getErrorStream();
                while ((len = inStr.read(buffer)) > 0) {
                    bais.write(buffer, 0, len);
                }

                String respText = bais.toString();
                System.out.println("RESP: " + respText);
            }
            badUrls.add(httpUrlConn.toString());
        }

        return null;
    }

    /**
     * Un-marshal an object from JSON.
     * 
     * @param jsonString JSON representation of the object
     * @return a model object
     */
    private static RootModel fromJSON(String jsonString) {
        try {
            return jsonReader.readValue(jsonString);
        } catch (JsonGenerationException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
            System.out.println("JSON: " + jsonString);
        } catch (JsonMappingException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
            System.out.println("JSON: " + jsonString);
        } catch (IOException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
            System.out.println("JSON: " + jsonString);
        }

        return null;
    }

    /**
     * Marshal an object to JSON.
     * 
     * @param model object to marshal
     * @return JSON representation of the object
     */
    private static String toJSON(RootModel model) {
        try {
            return jsonWriter.writeValueAsString(model);
        } catch (JsonGenerationException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        }

        return "";
    }
}
