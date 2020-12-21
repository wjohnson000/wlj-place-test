/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.bitplan.mediawiki.japi.Mediawiki;

/**
 * See: https://www.mediawiki.org/wiki/API:Revisions
 * <p/>
 * 
 * The following are the query parameters to include for each extraction ...
 * <ul>
 *   <li>action=query</li>
 *   <li>titles=...</li>
 *   <li>format=xml|json</li>
 *   <li>prop=revisions</li>
 *   <li>rvprop=content</li>
 *   <li>rvslots=main</li>
 *   <li>rvlimit=1</li>
 *   <li>rvsection=0,1</li>
 * </ul>
 * 
 * https://en.wikipedia.org/w/api.php?action=query&titles=Glorioso_Islands&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * https://sk.wikipedia.org/w/api.php?action=query&titles=Z%C3%A1padoslovensk%C3%BD_kraj&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * https://en.wikipedia.org/w/api.php?action=query&titles=Norðragøta&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * https://en.wikipedia.org/w/api.php?action=query&titles=Eastern_Region,_Uganda&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * https://be.wikipedia.org/w/api.php?action=query&titles=Віцебская_вобласць&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * https://fi.wikipedia.org/w/api.php?action=query&titles=Suomi&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * https://en.wikipedia.org/w/api.php?action=query&titles=A%27ana&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * https://wikishire.co.uk/w/api.php?action=query&titles=Cambridgeshire&format=xml&prop=revisions&rvprop=content&rvlimit=1&rvslots=main&rvsection=0
 * 
 * Munge the output:
 *   Unencode HTML tags &amp;nbsp; --> " "
 *   {{Convert...}} -- allow
 *   {{Coord...}} -- allow
 *   {{ ... }} -- exclude
 *   [[xxxx]] -- include "xxxx"
 *   [[File: ...]] -- exclude
 *   [[xxxx|yyyy]] -- include "yyyy"
 *   [[xxxx|yyyy|zzzz|aaaa...]] -- exclude
 *   ''' -- remove [look for first occurrence?]
 *   <table> -- exclude
 * 
 * @author wjohnson000
 *
 */
public class MediaWikiTest {

    static final String[][] pages = {
        { "en", "Glorioso_Islands" },
        { "sk", "Z%C3%A1padoslovensk%C3%BD_kraj" },
        { "en", "Norðragøta" },
        { "en", "Eastern_Region,_Uganda" },
        { "be", "Віцебская_вобласць" },
        { "fi", "Suomi" },
        { "en", "A%27ana" },
    };

    static final Map<String, Mediawiki> wikis = new HashMap<>();

    public static void main(String...args) throws Exception {
        for (String[] page : pages) {
            System.out.println();
            System.out.println("===================================================================================================");
            System.out.println("===================================================================================================");
            System.out.println(Arrays.toString(page));

            try {
                Mediawiki wiki = getWiki(page[0]);
                if (wiki == null) {
                    System.out.println("  WIKI not found ...");
                } else {
                    System.out.println("  " + wiki.getSiteInfo());
                    String content = wiki.getPageContent(page[1]);
                    System.out.println(content);
                }
            } catch(Exception ex) {
                System.out.println("Oops ... " + ex.getMessage());
            }
        }
    }

    static Mediawiki getWiki(String lang) throws Exception {
        Mediawiki wiki = wikis.get(lang);

        if (wiki == null) {
            wiki = new Mediawiki("https://" + lang + ".wikipedia.org");
            wikis.put(lang, wiki);
        }

        return wiki;
    }
}
