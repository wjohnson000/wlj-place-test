/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.familysearch.standards.loader.sql.FileResultSet;

/**
 * Three items:
 *   -- if a page has "xx.wikipedia.org" in the title, process it
 *   -- if a page has "familysearch.org" in the title, process it
 *   -- print a list of the top 20 other domains, with a count of how many entries match
 * @author wjohnson000
 *
 */
public class GenAttrUrlTitleEasy {

    private static final String FAMILYSEARCH = "familysearch.org/wiki/en/";
    private static final String WIKIPEDIA    = "en.wikipedia.org/wiki/";

    private static final String ATTR_FILE    = "C:/temp/db-dump/attribute-all.txt";
    private static final String TITLE_FILE   = "C:/temp/attr-url-title-easy.txt";

    public static void main(String... args) throws IOException {
        Map<String,String> urlTitle = new TreeMap<>();
        Map<String, Integer> domainCount = new TreeMap<>();

        int rows = 0;
        try (FileResultSet attrRS = new FileResultSet()) {
            attrRS.setSeparator("\\|");
            attrRS.openFile(ATTR_FILE);
            while (attrRS.next()) {
                if (++rows % 25_000 == 0) System.out.println("rows: " + rows);
                String value = attrRS.getString("attr_value");

                if (value != null  &&  value.startsWith("http")  &&  ! urlTitle.containsKey(value)) {
                    String title = titleFromWikipedia(value);
                    if (title == null) {
                        title = titleFromFamilySearchWiki(value);
                    }
                    if (title == null) {
                        String domain = getDomain(value);
                        Integer count = domainCount.getOrDefault(domain, 0);
                        domainCount.put(domain, count+1);
                    } else {
                        urlTitle.put(value, title);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("OOPS: " + ex.getMessage());
        }

        Files.write(Paths.get(TITLE_FILE), formatTitles(urlTitle), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        domainCount.entrySet().stream()
            .filter(entry -> entry.getValue() > 250)
            .forEach(System.out::println);
    }

    protected static List<String> formatTitles(Map<String, String> urlData) {
        return urlData.entrySet().stream()
            .map(entry -> entry.getKey() + "|" + entry.getValue())
            .collect(Collectors.toList());
    }

    protected static String titleFromWikipedia(String url) {
        String tUrl = unencode(url);
        int ndx = tUrl.indexOf(WIKIPEDIA);
        if (ndx < 0) {
            return null;
        } else {
            return tUrl.substring(ndx + WIKIPEDIA.length()).replace('_', ' ') + " - Wikipedia";
        }
    }

    protected static String titleFromFamilySearchWiki(String url) {
        String tUrl = unencode(url);
        int ndx = tUrl.indexOf(FAMILYSEARCH);
        if (ndx < 0) {
            return null;
        } else {
            return tUrl.substring(ndx + FAMILYSEARCH.length()).replace('_', ' ') + " - FamilySearch Wiki";
        }
    }

    protected static String getDomain(String url) {
        String tUrl = unencode(url);
        int ndx0 = tUrl.indexOf("//");
        int ndx1 = tUrl.indexOf('/', ndx0+3);
        if (ndx1 <= 0) {
            return "";
        } else {
            return tUrl.substring(ndx0+2, ndx1);
        }
    }

    protected static String unencode(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("URL: " + url + " --> " + ex.getMessage());
            return url;
        }
    }
}
