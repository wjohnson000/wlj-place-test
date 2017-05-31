package std.wlj.ws.rawhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.familysearch.standards.place.ws.model.RootModel;

public class HttpClientHelper {

    /** JSON object mapper */
    public static final ObjectMapper jsonMapper = new ObjectMapper();
    public static final ObjectReader jsonReader = jsonMapper.reader(RootModel.class);
    public static final ObjectWriter jsonWriter = jsonMapper.writerWithType(RootModel.class);

    /** XML object mapper */
    public static Unmarshaller xmlUnmarshaller;
    static {
    	try {
			JAXBContext context = JAXBContext.newInstance(RootModel.class);
			xmlUnmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) { }
    }

    /** Time-out values*/
    private static int readTimeOut = 120000;
    private static int connectTimeOut = 10000;

    /** Authorization header value */
    public static String authId = "";

    /** Accept header value */
    public static String acceptType = "";

    /** Content-Type header value */
    public static String contentType = "";

    /** Experiment (feature) header tags */
    public static String featureTag = "";

    /** Proxy information */
    public static String proxyDNS = null;
    public static int    proxyPort = 0;

    public static RootModel doGET(URL url) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        HttpGet httpGet = new HttpGet(url.toURI());

        return handleHttpRequest(httpClient, httpGet);
    }

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

    public static RootModel doDELETE(URL url) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        HttpDelete httpDelete = new HttpDelete(url.toURI());

        return handleHttpRequest(httpClient, httpDelete);
    }

    public static RootModel doHEAD(URL url) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        HttpHead httpHead = new HttpHead(url.toURI());

        return handleHttpRequest(httpClient, httpHead);
    }

    public static RootModel doOPTIONS(URL url) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        HttpOptions httpOptions = new HttpOptions(url.toURI());

        return handleHttpRequest(httpClient, httpOptions);
    }

    public static RootModel doPOST(URL url, RootModel model) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        HttpPost httpPost = new HttpPost(url.toURI());

        return handleHttpRequest(httpClient, httpPost, model);
    }

    public static RootModel doPUT(URL url, RootModel model) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        HttpPut httpPut = new HttpPut(url.toURI());

        return handleHttpRequest(httpClient, httpPut, model);
    }

    protected static CloseableHttpClient createHttpClient() throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(connectTimeOut)
            .setSocketTimeout(readTimeOut).build();

        return HttpClients
                .custom()
                .setDefaultRequestConfig(requestConfig)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(
                        SSLContexts.custom()
                            .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                            .build())
                ).build();
    }

    protected static RootModel handleHttpRequest(CloseableHttpClient httpClient, HttpRequestBase httpMethod) {
        addRequestHeaders(httpMethod);

        try(CloseableHttpResponse response = httpClient.execute(httpMethod)) {
            RootModel rootModel = handleResponse(response);
            return rootModel;
        } catch(Exception ex) {
            System.out.println("EX: " + ex.getMessage());
            return null;
        }
    }
    
    protected static RootModel handleHttpRequest(CloseableHttpClient httpClient, HttpEntityEnclosingRequestBase httpMethod, RootModel model) throws UnsupportedEncodingException {
        addRequestHeaders(httpMethod);
        addEntity(httpMethod, model);
        System.out.println(httpMethod);

        try(CloseableHttpResponse response = httpClient.execute(httpMethod)) {
            RootModel rootModel = handleResponse(response);
            return rootModel;
        } catch(Exception ex) {
            System.out.println("EX: " + ex.getMessage());
            return null;
        }
    }

    protected static void addRequestHeaders(HttpRequestBase httpMethod) {
        httpMethod.addHeader("Authorization", authId);
        httpMethod.addHeader("Accept-Language", "en");
        httpMethod.addHeader("Accept-Charset", "utf-8");

        if (acceptType == null  ||  acceptType.trim().isEmpty()) {
            httpMethod.addHeader("Accept", RootModel.APPLICATION_JSON_PLACES);
        } else {
            httpMethod.addHeader("Accept", acceptType);
        }

        if (contentType == null  ||  contentType.trim().isEmpty()) {
            httpMethod.addHeader("Content-Type", "application/json");
        } else {
            httpMethod.addHeader("Content-Type", contentType);
        }

        if (featureTag != null  &&  featureTag.trim().length() > 0) {
            httpMethod.addHeader("X-FS-Feature-Tag", featureTag);
        }
    }

    private static void addEntity(HttpEntityEnclosingRequestBase httpMethod, RootModel model) throws UnsupportedEncodingException {
        String json = toJSON(model);

        ByteArrayEntity baEntity = new ByteArrayEntity(json.getBytes("UTF-8"));
        baEntity.setContentType("application/standards-places-v2+json");
        baEntity.setContentEncoding("UTF-8");

        httpMethod.setEntity(baEntity);
    }

    /**
     * Handle the response of the HttpURLConnection.
     * 
     * @param httpUrlConn HttpURLConnection
     * 
     * @return Marshaled data, or null if no response body, or error
     * @throws Exception
     */
    private static RootModel handleResponse(CloseableHttpResponse response) throws Exception {
        System.out.println("STTS: " + response.getStatusLine());
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            System.out.println("HDR: " + header.getName() + " --> " + header.getValue());
        }

        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        InputStream inStr = entity.getContent();

        if (status == 200  ||  status == 201) {
            int len = 0;
            byte[] buffer = new byte[1024*16];
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            while (inStr != null  &&  (len = inStr.read(buffer)) > 0) {
                bais.write(buffer, 0, len);
            }

            String respText = bais.toString();
            String mimeType = getResponseMimeType(response);
            if (mimeType.toLowerCase().contains("xml")) {
                return fromXML(respText);
            } else if (mimeType.toLowerCase().contains("json")) {
                return fromJSON(respText);
            } else {
                System.out.println("Mime: " + mimeType);
                System.out.println("RESP: " + respText);
            }
        } else if (status == 301  ||  status == 302  ||  status == 303 || status == 307) {
            String location = getLocation(response);
            System.out.println("[RE-DIRECT]: " + location);
        } else if (status != 404) {
            if (inStr != null) {
                int len = 0;
                byte[] buffer = new byte[1024*16];
                ByteArrayOutputStream bais = new ByteArrayOutputStream();
                while (inStr != null  &&  (len = inStr.read(buffer)) > 0) {
                    bais.write(buffer, 0, len);
                }

                String respText = bais.toString();
                System.out.println("RESP: " + respText);
            }
        }

        return null;
    }

    private static String getResponseMimeType(CloseableHttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase("content-type")) {
                return header.getValue();
            }
        }

        return "unknown";
    }

    private static String getLocation(CloseableHttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase("location")) {
                return header.getValue();
            }
        }

        return "unknown";
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
     * Un-marshal an object from XML.
     * 
     * @param xmlString JSON representation of the object
     * @return a model object
     */
    private static RootModel fromXML(String xmlString) {
    	try {
            StringReader reader = new StringReader(xmlString);
            return (RootModel)xmlUnmarshaller.unmarshal(reader);
        } catch (JAXBException | NullPointerException e) {
            System.out.println("Unable to marshal: " + e.getMessage());
            return null;
        }
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
