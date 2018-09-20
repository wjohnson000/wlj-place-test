package std.wlj.wikipedia;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.wikipedia.better.WikiQueryHandler;

public class ParseWikiLots {

    public static void main(String...args) throws Exception {
        long timeA = 0L;
        long timeB = 0L;
        long timeC = 0L;
        long time0;

        List<String> urls = Files.readAllLines(Paths.get("C:/temp/wiki-link.txt"), Charset.forName("UTF-8"));
        for (int ndx=0;  ndx<urls.size();  ndx+=197) {
            String urlX = urls.get(ndx);
            String[] url = PlaceHelper.split(urlX, '\t');

            WikiSaxHandler saxHandler = new WikiSaxHandler();
            WikiQuerySaxHandler queryHandler = new WikiQuerySaxHandler();
            WikiQueryHandler newHandler = new WikiQueryHandler(url[1]);

            System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("URL: " + url[1]);

            time0 = System.nanoTime();
            List<String> wikiA = saxHandler.parseWikiSAX(url[1]);
            timeA += (System.nanoTime() - time0);

            time0 = System.nanoTime();
            List<String> wikiB = queryHandler.parseWikiSAX(url[1]);
            timeB += (System.nanoTime() - time0);

            time0 = System.nanoTime();
            List<String> wikiC = newHandler.parseWikiSAX();
            timeC += (System.nanoTime() - time0);

            wikiA.forEach(ww -> System.out.println("  A:" + ww));
            wikiB.forEach(ww -> System.out.println("  B:" + ww));
            wikiC.forEach(ww -> System.out.println("  C:" + ww));
        }

        System.out.println("\n\n\nTimeA: " + (timeA / 1_000_000.0));
        System.out.println("TimeB: " + (timeB / 1_000_000.0));
        System.out.println("TimeC: " + (timeC / 1_000_000.0));

        System.exit(0);
    }
}
