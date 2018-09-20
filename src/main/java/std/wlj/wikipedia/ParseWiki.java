package std.wlj.wikipedia;

import java.util.List;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import std.wlj.wikipedia.better.WikiQueryHandler;

public class ParseWiki {

    private static String[] urls = {
//        "https://en.wikipedia.org/wiki/London,_England",
//        "https://en.wikipedia.org/wiki/Koshi_Zone",
//        "https://en.wikipedia.org/wiki/Duquesne",
//        "https://en.wikipedia.org/wiki/General Tinio,_Nueva Ecija",  // unexpanded template -- {{PH wikidata|...}}
//        "https://en.wikipedia.org/wiki/Aringay,_La Union",
//        "https://en.wikipedia.org/wiki/Lobelhe do Mato",
//        "https://en.wikipedia.org/wiki/Mabolo",
//        "https://pt.wikipedia.org/wiki/Prov%C3%ADncias_da_Guin%C3%A9_Equatorial",
//        "https://en.wikipedia.org/wiki/Pandan,_Catanduanes",
//        "https://en.wikipedia.org/wiki/Ajuy,_Iloilo",
//        "https://en.wikipedia.org/wiki/Friesoythe",
//        "https://en.wikipedia.org/wiki/Oekene",
//        "https://en.wikipedia.org/wiki/Barranquilla_Colombia_Temple",
//        "https://en.wikipedia.org/wiki/Bakersfield,_Missouri#History",
//        "https://en.wikipedia.org/wiki/Hebrides",
//        "https://sk.wikipedia.org/wiki/Pre%C5%A1ovsk%C3%BD_kraj",
//        "https://en.wikipedia.org/wiki/Maine",
//        "https://fr.wikipedia.org/wiki/13e_arrondissement_de_Paris",
//        "https://en.wikipedia.org/wiki/Rexburg,_Idaho",
//        "https://en.wikipedia.org/wiki/Yugoslavia",
//        "https://books.google.com/books?id=-84_kkgMf2QC&hl=en",
//        "https://en.wikipedia.org/wiki/Canada",
//        "https://en.wikipedia.org/wiki/Totora",
//        "https://fi.wikipedia.org/wiki/Suomi",
//        "https://lb.wikipedia.org/wiki/Distrikt_Gréiwemaacher",
//        "https://en.wikipedia.org/wiki/Glorioso_Islands",
//        "https://sk.wikipedia.org/wiki/Z%C3%A1padoslovensk%C3%BD_kraj",
//        "https://en.wikipedia.org/wiki/Norðragøta",
//        "https://en.wikipedia.org/wiki/Eastern_Region,_Uganda",
//        "https://be.wikipedia.org/wiki/Віцебская_вобласць",
//        "https://en.wikipedia.org/wiki/A%27ana",
//        "https://wikishire.co.uk/wiki/Cambridgeshire",
//        "https://zh.wikipedia.org/wiki/%E7%BA%BD%E7%BA%A6",
//        "https://ru.wikipedia.org/wiki/%D0%9D%D1%8C%D1%8E-%D0%99%D0%BE%D1%80%D0%BA",
        "https://de.wikipedia.org/wiki/New_York_City",
        "https://en.wikipedia.org/wiki/New_York_City",
    };

    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(2);
        httpConnManager.setDefaultMaxPerRoute(2);
    }

    public static void main(String...args) {
        for (String url : urls) {
            WikiSaxHandler saxHandler = new WikiSaxHandler();
            WikiQuerySaxHandler queryHandler = new WikiQuerySaxHandler();
            WikiQueryHandler newHandler = new WikiQueryHandler(url);

            List<String> wikiA = saxHandler.parseWikiSAX(url);
            List<String> wikiB = queryHandler.parseWikiSAX(url);
            List<String> wikiC = newHandler.parseWikiSAX();

            System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("URL: " + url);
            wikiA.forEach(ww -> System.out.println("  A:" + ww));
            wikiB.forEach(ww -> System.out.println("  B:" + ww));
            wikiC.forEach(ww -> System.out.println("  C:" + ww));
        }

        httpConnManager.close();
        System.exit(0);
    }
}
