package std.wlj.ws.rawhttp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import javax.ws.rs.HttpMethod;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.familysearch.standards.place.ws.model.kml.KmlModel;


public class HttpHelperKML {

    /** XML object mapper */
    public static Unmarshaller xmlUnmarshaller;
    static {
    	try {
			JAXBContext context = JAXBContext.newInstance(KmlModel.class);
			xmlUnmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) { }
    }

    /** Time-out values*/
    private static int readTimeOut = 120000;
    private static int connectTimeOut = 10000;

    /** Authorization header value */
    public static String authId = "";

    /** Experiment (feature) header tags */
    public static String featureTag = "";

    /** Proxy information */
    public static String proxyDNS = null;
    public static int    proxyPort = 0;

    /** Override HTTPS stuff */
    public static boolean overrideHTTPS = false;

    private static TrustManager[] trustAllCerts = new TrustManager[] {
    	new X509TrustManager() {
    		public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
    		public void checkClientTrusted(X509Certificate[] certs, String authType) { }
    		public void checkServerTrusted(X509Certificate[] certs, String authType) { }
    	}
	};

    private static HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) { return true; }
    };

    /**
     * Do a GET operation against the url ... NOTE: this method should not be used
     * if there are parameters that need to be url-encoded
     * 
     * @param url url
     * @return new model object, or whatever the service returns
     */
    public static KmlModel doGET(URL url) throws Exception {
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
    public static KmlModel doGET(URL url, String... keyVal) throws Exception {
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
     * The difference between a WebConstants.METHOD_GET, WebConstants.METHOD_DELETE, "HEAD" and a "OPTIONS" interface is minimal.
     * All of those actions come here to do the *real* work.
     * 
     * @param url URL to hit
     * @param method HTTP method, WebConstants.METHOD_GET, WebConstants.METHOD_DELETE, "HEAD" or "OPTIONS"
     * 
     * @return result of the operation
     * @throws Exception if something bad happens
     */
    public static KmlModel doRequest(URL url, String method) throws Exception {
        URLConnection urlConn = setupConnection(url);
        System.out.println(method + " --> " + urlConn.getURL());

        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
            httpUrlConn.setRequestMethod(method);
            httpUrlConn.setConnectTimeout(300000);
            HttpURLConnection.setFollowRedirects(true);

            if (overrideHTTPS) {
            	SSLContext ssl = SSLContext.getInstance("SSL");
            	ssl.init(null, trustAllCerts, new java.security.SecureRandom());
            	HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
            	HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            }

            httpUrlConn.connect();
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
        URL userUrl = url;
        if (url.toString().contains(" ")) {
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
            userUrl = uri.toURL();
        }

        if (proxyDNS == null) {
            urlConn = userUrl.openConnection();
        } else {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyDNS, proxyPort));
            urlConn = userUrl.openConnection(proxy);
        }

        urlConn.setConnectTimeout(connectTimeOut);
        urlConn.setReadTimeout(readTimeOut);
        urlConn.setRequestProperty("Authorization", authId);
        urlConn.setRequestProperty("Accept-Language", "en");
        urlConn.setRequestProperty("Accept-Charset", "utf-8");
        urlConn.setRequestProperty("Accept", KmlModel.APPLICATION_XML_KML);

        if (featureTag != null  &&  featureTag.trim().length() > 0) {
            urlConn.setRequestProperty("X-FS-Feature-Tag", featureTag);
        }

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
    private static KmlModel handleResponse(HttpURLConnection httpUrlConn) throws Exception {
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
            return fromXML2Kml(respText);
        } else if (status == 301  ||  status == 302  ||  status == 303) {
        	String location = httpUrlConn.getHeaderField("Location");
        	System.out.println("[RE-DIRECT]: " + location);
        	return doRequest(new URL(location), HttpMethod.GET);
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
        }

        return null;
    }

    /**
     * Un-marshal an object from XML.
     * 
     * @param xmlString JSON representation of the object
     * @return a model object
     */
    private static KmlModel fromXML2Kml(String xmlString) {
        try {
            StringReader reader = new StringReader(xmlString);
            return (KmlModel)xmlUnmarshaller.unmarshal(reader);
        } catch (JAXBException | NullPointerException e) {
            System.out.println("Unable to marshal: " + e.getMessage());
            return null;
        }
    }
}
