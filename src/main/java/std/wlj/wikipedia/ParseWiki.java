package std.wlj.wikipedia;

import java.util.List;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ParseWiki {

    private static String[] urls = {
        "https://en.wikipedia.org/wiki/Barranquilla_Colombia_Temple",
        "https://en.wikipedia.org/wiki/Bakersfield,_Missouri#History",  
        "https://en.wikipedia.org/wiki/Hebrides",
        "https://sk.wikipedia.org/wiki/Pre%C5%A1ovsk%C3%BD_kraj",
        "https://en.wikipedia.org/wiki/Maine",
        "https://fr.wikipedia.org/wiki/13e_arrondissement_de_Paris",
        "https://en.wikipedia.org/wiki/Rexburg,_Idaho",
        "https://en.wikipedia.org/wiki/Yugoslavia",
        "https://books.google.com/books?id=-84_kkgMf2QC&hl=en"
    };
    
    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(2);
        httpConnManager.setDefaultMaxPerRoute(2);
    }

    public static void main(String...args) {
        for (String url : urls) {
            WikiSaxHandler saxHandler = new WikiSaxHandler();
            List<String> wikiZ = saxHandler.parseWikiSAX(url);
            System.out.println("URL: " + url);
            System.out.println("\n" + wikiZ + "\n\n");
            System.out.println("   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }

        httpConnManager.close();
        System.exit(0);
    }
}
