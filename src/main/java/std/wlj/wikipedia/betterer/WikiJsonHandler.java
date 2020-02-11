/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia.betterer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author wjohnson000
 *
 */
public class WikiJsonHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern wikiPattern = Pattern.compile("(?:(http[s]{0,1}://.*))/wiki/(?:(.*))$");
    private static final String  wikiQueryURL = "%s/w/api.php?action=query&format=json&prop=extracts" +
                                                "&siprop=rightsinfo&meta=siteinfo&exintro&explaintext" +
                                                "&redirects=1&titles=%s";

    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(2);
        httpConnManager.setDefaultMaxPerRoute(2);
    }

    public static String parseWiki(String wikiUrl) {
        String apiUrl = resolveApiUrl(wikiUrl);
        if (apiUrl == null) {
            return null;
        } else {
            String rawJson = getRawJson(apiUrl);
            System.out.println("  WikiURL: " + apiUrl);
            System.out.println("      RAW: " + rawJson);
            String contents = getContents(rawJson);
            return contents;
        }
    }

    protected static String resolveApiUrl(String wikiUrl) {
        Matcher matcher = wikiPattern.matcher(wikiUrl);
        if (matcher.matches()) {
            String baseUrl  = matcher.group(1);
            String pageName = matcher.group(2).trim().replace(' ', '_');
            return String.format(wikiQueryURL, baseUrl, pageName);
        } else {
            return null;
        }
    }

    protected static String getRawJson(String url) {
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "text/html");

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            String json = IOUtils.toString(ios, StandardCharsets.UTF_8);
            EntityUtils.consumeQuietly(response.getEntity());
            return json;
        } catch (Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
            return null;
        }
    }

    protected static String getContents(String rawJson) {
        try {
            JsonNode node  = objectMapper.readTree(rawJson);
            JsonNode pages = node.path("query").path("pages");
            ObjectNode oPages = (ObjectNode)pages;
            JsonNode page = oPages.fields().next().getValue();
            return page.get("extract").asText();
        } catch (Exception e) {
            // May want to log this before returning "null" ...
            return null;
        }
    }
}
