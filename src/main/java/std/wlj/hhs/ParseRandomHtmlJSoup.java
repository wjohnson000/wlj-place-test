/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 * @author wjohnson000
 *
 */
public class ParseRandomHtmlJSoup {

    static String[] tests = {
        "<h2>The Head</h2><p>Paragraph <i>one</i>.</p><p>Paragraph <b>two</b> has stuff <link>go-here</link></p>",
        "<h2>The Head<p>Paragraph <i>one</i>.<p>Paragraph <b>two</b><ul><li>one</li><li>two</ul>",
    };

    public static void main(String...args) {
        for (String test : tests) {
            try {
                System.out.println("===========================================================");
                System.out.println("HTML: " + test);

                Document htmlDoc = Jsoup.parse(test);
                Elements bodyX = htmlDoc.getElementsByTag("body");
                if (bodyX.size() == 1) {
                    dumpElement(0, bodyX.get(0));
                }
                Elements allX = htmlDoc.getAllElements();
                allX.stream().forEach(ee -> System.out.println("XX: " + ee.tagName() + "::" + ee.ownText()));
            } catch(Exception ex) {
                System.out.println("Oops: " + ex.getMessage());
            }
        }
    }

    static void dumpElement(int level, Element elem) {
        Elements children = elem.children();
        if (children.size() == 0) {
            System.out.println(level + ".B:" + elem.tagName() + "::" + elem.html());
        } else {
            final int levelx = level++;
            System.out.println(level + ".A:" + elem.tagName() + "::" + elem.html());
            children.stream().forEach(cc -> dumpElement(levelx, cc));
        }
    }
}
