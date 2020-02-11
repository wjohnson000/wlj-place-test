/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia.betterer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * @author wjohnson000
 *
 */
public class WikipediaWithJson {

    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(2);
        httpConnManager.setDefaultMaxPerRoute(2);
    }

    private static String wikiUrl =
        "https://%s.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=%s";

    private static String[][] urls = {
        { "en", "London,_England" },
        { "en", "Koshi_Zone" },
        { "en", "Duquesne" },
        { "en", "General Tinio,_Nueva Ecija" },  // unexpanded template -- {{PH wikidata|...}}
        { "en", "Aringay,_La Union" },
        { "en", "Lobelhe do Mato" },
        { "en", "Mabolo" },
        { "en", "Pandan,_Catanduanes" },
        { "en", "Ajuy,_Iloilo" },
        { "en", "Friesoythe" },
        { "en", "Oekene" },
        { "en", "Barranquilla_Colombia_Temple" },
        { "en", "Bakersfield,_Missouri#History" },
        { "en", "Hebrides" },
        { "en", "Maine" },
        { "en", "Rexburg,_Idaho" },
        { "en", "Yugoslavia" },
        { "en", "Canada" },
        { "en", "Totora" },
        { "en", "Glorioso_Islands" },
        { "en", "Norðragøta" },
        { "en", "Eastern_Region,_Uganda" },
        { "en", "A%27ana" },
        { "en", "New_York_City" },
        { "en", "Aowin/Suaman_District" },

        { "be", "Віцебская_вобласць" },
        { "de", "New_York_City" },
        { "fi", "Suomi" },
        { "fr", "13e_arrondissement_de_Paris" },
        { "lb", "Distrikt_Gréiwemaacher" },
        { "pt", "Prov%C3%ADncias_da_Guin%C3%A9_Equatorial" },
        { "ru", "%D0%9D%D1%8C%D1%8E-%D0%99%D0%BE%D1%80%D0%BA" },
        { "sk", "Pre%C5%A1ovsk%C3%BD_kraj" },
        { "sk", "Z%C3%A1padoslovensk%C3%BD_kraj" },
        { "zh", "%E7%BA%BD%E7%BA%A6" },
    };

    private static String[] otherUrls = {
        "wikishire.co.uk/wiki/Cambridgeshire"
    };

    public static void main(String...args) {
        for (String[] url : urls) {
            String newUrl = String.format(wikiUrl, url[0], url[1]).replaceAll(" ", "%20");
            System.out.println("\nURL: " + newUrl);
            getContents(newUrl);
        }
    }

    static void getContents(String url) {
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "text/html");

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            String json = IOUtils.toString(ios, StandardCharsets.UTF_8);
            EntityUtils.consumeQuietly(response.getEntity());
            System.out.println(json);
        } catch (Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
        }
    }
   
}
