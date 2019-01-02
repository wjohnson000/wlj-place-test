package std.wlj.wikipedia;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class ParseTitle {

    private static String[] urls = {
//        "https://familysearch.org/wiki/en/Burundi_Genealogy",
//        "https://en.wikipedia.org/wiki/Barranquilla_Colombia_Temple",
//        "https://en.wikipedia.org/wiki/Bakersfield,_Missouri#History",  
//        "https://en.wikipedia.org/wiki/Hebrides",
//        "https://sk.wikipedia.org/wiki/Pre%C5%A1ovsk%C3%BD_kraj",
//        "https://en.wikipedia.org/wiki/Maine",
//        "https://fr.wikipedia.org/wiki/13e_arrondissement_de_Paris",
//        "https://en.wikipedia.org/wiki/Rexburg,_Idaho",
        "http://www.ajfand.net/Volume5/No2/commentaryDominicus.html",
    };
    
    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(2);
        httpConnManager.setDefaultMaxPerRoute(2);
    }

    public static void main(String...args) {
        for (String url : urls) {
            getTitleOne(url);
            getTitleTwo(url);
        }

        httpConnManager.close();
        System.exit(0);
    }

    static void getTitleOne(String url) {
        long time0 = System.nanoTime();
        TitleSaxHandler titleHandler = new TitleSaxHandler();
        String title = titleHandler.parseTitle(url);
        long time1 = System.nanoTime();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("one.URL: " + url);
        System.out.println("     TT: " + title);
        System.out.println("      T: " + (time1-time0)/1_000_000.0);
    }

    static void getTitleTwo(String url) {
        String title = null;

        long time0 = System.nanoTime();
        String rawHtml = getRawHtml(url);
        if (rawHtml != null) {
            TitleSaxHandler titleHandler = new TitleSaxHandler();
            title = titleHandler.parseTitleFromHtml(rawHtml);
        }
        long time1 = System.nanoTime();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("two.URL: " + url);
        System.out.println("     TT: " + title);
        System.out.println("      T: " + (time1-time0)/1_000_000.0);
    }

    static String getRawHtml(String url) {
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "text/html");

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            String html = IOUtils.toString(ios, StandardCharsets.UTF_8);
            EntityUtils.consumeQuietly(response.getEntity());
            return html;
        } catch (Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
            return null;
        }
    }
}
