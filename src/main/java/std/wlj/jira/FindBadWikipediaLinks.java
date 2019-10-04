/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.wikipedia.better.WikiQueryHandler;

/**
 * @author wjohnson000
 *
 */
public class FindBadWikipediaLinks {

    static final String baseDir     = "C:/temp/db-dump";
    static final String attrFile    = "attribute-all.txt";

    public static void main(String... args) throws Exception {
//        String blah = "https://en.wikipedia.org/wiki/Little_Summer_Island|Little Summer Island - Wikipedia";
//        String blah = "https://en.wikipedia.org/wiki/Liberia";
        String blah = "https://en.wikipedia.org/wiki/Sao_Tome_and_Principe";
        WikiQueryHandler wikiHandlerX = new WikiQueryHandler(blah);
        List<String> paragraphsX = wikiHandlerX.parseWikiSAX();
        System.out.println("   " + paragraphsX.size() + " --> " + paragraphsX.get(0));
        System.out.println("   " + paragraphsX.size() + " --> " + paragraphsX.get(1));

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, attrFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("ATTR.read: " + lineCnt);

                String attrData = scan.nextLine();
                String[] chunks = PlaceHelper.split(attrData, '|');
                if (chunks.length > 5) {
                    String repId  = chunks[0];
                    String attrId = chunks[1];
                    String typeId = chunks[3];
                    String url    = chunks[9];

                    if ("474".equals(typeId)) {
                        try {
                            WikiQueryHandler wikiHandler = new WikiQueryHandler(url);
                            List<String> paragraphs = wikiHandler.parseWikiSAX();
                            if (paragraphs == null  ||  paragraphs.isEmpty()) {
                                System.out.println(repId + "|" + attrId + "|" + url);
                                System.out.println("   empty --> no results ...");
                            } else if (paragraphs.size() == 1  &&  paragraphs.get(0).length() < 15) {
                                System.out.println(repId + "|" + attrId + "|" + url);
                                System.out.println("   " + paragraphs.size() + " --> " + paragraphs.get(0));
                            } else if (paragraphs.size() == 2  &&  paragraphs.get(0).length() < 15  &&  paragraphs.get(1).length() < 15) {
                                System.out.println(repId + "|" + attrId + "|" + url);
                                System.out.println("   1 --> " + paragraphs.get(0));
                                System.out.println("   2 --> " + paragraphs.get(1));
                            } else if (paragraphs.size() == 2  &&  paragraphs.get(0).length() < 15) {
                                System.out.println(repId + "|" + attrId + "|" + url);
                                System.out.println("   1 --> " + paragraphs.get(0));
                                System.out.println("   2 --> " + paragraphs.get(1));
                            }
                        } catch(Exception ex) {
                            System.out.println("   OOPS!!  " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
