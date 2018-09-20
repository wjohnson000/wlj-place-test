/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia.better;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public final class WikiQueryHandler {

    private static final Set<String> REMOVE_TEXT = new HashSet<>();
    static {
        REMOVE_TEXT.add("__NOTOC__");
    }

    private static final Set<String> NSEW = new HashSet<>();
    static {
        NSEW.add("N");
        NSEW.add("S");
        NSEW.add("E");
        NSEW.add("W");
    }

    private static final String[] DEG_MIN_SEC = { "°", "′", "″" };

    private static final Pattern wikiPattern = Pattern.compile("(?:(http[s]://.*))/wiki/(?:(.*))$");
    private static final String  wikiQueryURL = "%s/w/api.php?action=query&titles=%s&format=xml&prop=revisions&" +
                                                "rvprop=content&rvlimit=1&rvslots=main&rvsection=0";
    private static final String  wikiTemplateURL = "%s/w/api.php?action=expandtemplates&title=%s&format=xml&prop=wikitext&text=";

    private String wikiURL;
    private String queryURL;
    private String templateURL;
    private WikiSaxHandler saxHandler;

    public WikiQueryHandler(String wikiURL) {
        this.wikiURL = removeAnchorTag(wikiURL);
        this.saxHandler = new WikiSaxHandler();
    }

    public List<String> parseWikiSAX() {
        setUrls();
        if (queryURL == null) {
            return Collections.emptyList();
        } else {
            int count = 0;
            String content = saxHandler.parseWikiSAX(queryURL);
            while (content.toLowerCase().trim().startsWith("#redirect")  &&  count++ < 3) {
                setUrls(content);
                content = saxHandler.parseWikiSAX(queryURL);
            }
            return cleanResults(content);
        }
    }

    protected String removeAnchorTag(String wikiURL) {
        int ndx = wikiURL.indexOf('#');
        return (ndx == -1) ? wikiURL : wikiURL.substring(0, ndx);
    }

    protected void setUrls() {
        Matcher matcher = wikiPattern.matcher(wikiURL);
        if (matcher.matches()) {
            String baseUrl  = matcher.group(1);
            String pageName = matcher.group(2).trim().replace(' ', '_');
            queryURL = String.format(wikiQueryURL, baseUrl, pageName);
            templateURL = String.format(wikiTemplateURL, baseUrl, pageName);
        }
    }

    protected void setUrls(String redirect) {
        String tRedirect = redirect.trim().substring(9);
        int ndx = tRedirect.indexOf(']');
        tRedirect = tRedirect.substring(0, ndx);
        tRedirect = tRedirect.replace('[', ' ').replace(']', ' ').trim();
        tRedirect = tRedirect.replace(' ', '_');
        
        Matcher matcher = wikiPattern.matcher(wikiURL);
        if (matcher.matches()) {
            String baseUrl  = matcher.group(1);
            String pageName = tRedirect;
            queryURL = String.format(wikiQueryURL, baseUrl, pageName);
            templateURL = String.format(wikiTemplateURL, baseUrl, pageName);
        }
    }

    /**
     * Remove unnecessary data from the raw results and split the remainder into multiple paragraphs:
     * <ul>
     *   <li>Unencode HTML tags</li>
     *   <li>Process "{{Convert ...}}" sections</li>
     *   <li>Process "{{Coord ...}}" sections</li>
     *   <li>Process "{{XX wikidata ...}}" sections</li>
     *   <li>Ignore all other "{{...}}" sections</li>
     *   <li>Remove triple quotes</li>
     *   <li>Process "[[...]]" sections that contain no pipe (|) characters</li>
     *   <li>Process "[[...|...]] sections that contain a single pipe (|) character</li>
     *   <li>Exclude "[[...|...|...]] sections that contain a multiple pipe (|) character</li>
     * </ul>
     */
    protected List<String> cleanResults(String text) {
        String tText = text;

        tText = unencodeHtml(tText);
        tText = handleRemovals(tText);
        tText = handleTables(tText);
        tText = handleInfobox(tText);
        tText = handleCurlyBraceSections(tText);
        tText = handleSquareBraceSections(tText);
        tText = handleRefSections(tText);
        tText = handleHttpCommentsSections(tText);
        tText = handleMultipleQuotes(tText);
        tText = handleEmptyParenthesis(tText);

        String[] chunks = PlaceHelper.split(tText, '\n');
        return Arrays.stream(chunks)
            .map(txt -> txt.trim())
            .filter(txt -> ! txt.isEmpty())
            .filter(txt -> ! txt.startsWith("|"))
            .limit(2)
            .collect(Collectors.toList());
    }

    protected String unencodeHtml(String text) {
        return StringEscapeUtils.unescapeHtml(text);
    }

    protected String handleRemovals(String text) {
        String tText = text;
        for (String remove : REMOVE_TEXT) {
            int ndx = tText.indexOf(remove);
            while (ndx >= 0) {
                tText = ((ndx == 0) ? "" : tText.substring(0, ndx)) + tText.substring(ndx+remove.length());
                ndx = tText.indexOf(remove);
            }
        }

        return tText;
    }

    protected String handleTables(String text) {
        String tText = text;
        StringBuilder buff = new StringBuilder();

        int ndx0 = tText.toUpperCase().indexOf("<TABLE");
        while (ndx0 >= 0) {
            int ndx1 = tText.toUpperCase().indexOf("</TABLE>", ndx0);
            buff.append((ndx0 == 0) ? "" : tText.substring(0, ndx0));
            if (ndx1+8 > tText.length()) {
                tText = "";
            } else {
                tText = tText.substring(ndx1+8);
            }

            ndx0 = tText.toUpperCase().indexOf("<TABLE");
        }
        buff.append(tText);

        return buff.toString();
    }

    protected String handleInfobox(String text) {
        int begBrace = text.toUpperCase().indexOf("{{INFOBOX");
        return (begBrace <= 1) ? text : text.substring(begBrace-1);
    }

    protected String handleCurlyBraceSections(String text) {
        String tText = text;

        int level = 1;
        int begBrace = tText.indexOf("{{");
        int curBrace = begBrace;
        while (begBrace >= 0) {
            int ndx0 = tText.indexOf("{{", curBrace + 2);
            int ndx1 = tText.indexOf("}}", curBrace + 2);

            if (ndx0 > 0  &&  ndx0 < ndx1) {
                curBrace = ndx0;
                level++;
            } else if (ndx1 > 0) {
                curBrace = ndx1;
                level--;
                if (level == 0) {
                    tText = tText.substring(0, begBrace) + extractCurlyBraceText(tText.substring(begBrace+2, curBrace)) + tText.substring(curBrace+2);
                    level = 1;
                    begBrace = tText.indexOf("{{");
                    curBrace = begBrace;
                }
            } else {
                begBrace = -1;  // This means there is no closing brace
            }
        }

        begBrace = tText.indexOf("{{");
        curBrace = tText.indexOf("}}");
        while (curBrace >= 0  &&  (curBrace < begBrace  ||  begBrace == -1)) {
            tText = tText.substring(curBrace + 2);
            begBrace = tText.indexOf("{{");
            curBrace = tText.indexOf("}}");
        }

        return tText.trim();
    }

    protected String handleMultipleQuotes(String text) {
        return text.replaceAll("'''", "").replaceAll("''", "").trim();
    }

    protected String extractCurlyBraceText(String text) {
        String result = "";

        String[] chunks = PlaceHelper.split(text, '|');
        if (chunks[0].equalsIgnoreCase("convert")) {
            result = extractConvert(text);
        } else if (chunks[0].equalsIgnoreCase("coord")) {
            result = extractCoords(text);
        } else if (chunks[0].equalsIgnoreCase("formatnum")) {
            result = extractNumber(text);
        } else if (chunks[0].contains("wikidata")) {
            return expandTemplate(text);
        } else if (chunks.length == 2) {
            if (chunks[0].toUpperCase().startsWith("LANG-")) {
                result = "(" + chunks[0] + " " + chunks[1] + ")"; 
            } else if (chunks[0].equalsIgnoreCase("km2")) {
                result = chunks[0] + " " + chunks[1]; 
            } else if (chunks[1].equals("arrondissement")) {
                result = chunks[0] + " " + chunks[1]; 
            }
        }

        return result;
    }

    protected String extractConvert(String text) {
        String[] chunks = PlaceHelper.split(text, '|');
        if (chunks.length > 2) {
            return chunks[1] + chunks[2];
        } else {
            return "???";
        }
    }

    protected String extractNumber(String text) {
        String[] chunks = PlaceHelper.split(text, ':');
        if (chunks.length > 1) {
            return chunks[1];
        } else {
            return "???";
        }
    }

    protected String extractCoords(String text) {
        StringBuilder buff = new StringBuilder();

        int measure = 0;
        int nsewCnt = 0;
        String[] chunks = PlaceHelper.split(text, '|');
        for (String chunk : chunks) {
            if (chunk.equalsIgnoreCase("coord")  ||  nsewCnt == 2) {
                ;  // Do nothing
            } else if (NSEW.contains(chunk.toUpperCase())) {
                buff.append(chunk).append(' ');
                nsewCnt++;
                measure = 0;
            } else {
                buff.append(chunk);
                buff.append((measure < DEG_MIN_SEC.length) ? DEG_MIN_SEC[measure] : "");
                measure++;
            }
        }

        return buff.toString().trim();
    }

    protected String handleSquareBraceSections(String text) {
        String tText = text;

        int level = 1;
        int begBrace = tText.indexOf("[[");
        int curBrace = begBrace;
        while (begBrace >= 0) {
            int ndx0 = tText.indexOf("[[", curBrace + 2);
            int ndx1 = tText.indexOf("]]", curBrace + 2);

            if (ndx0 > 0  &&  ndx0 < ndx1) {
                curBrace = ndx0;
                level++;
            } else if (ndx1 > 0) {
                curBrace = ndx1;
                level--;
                if (level == 0) {
                    tText = tText.substring(0, begBrace) + extractSquareBraceText(tText.substring(begBrace+2, curBrace)) + tText.substring(curBrace+2);
                    level = 1;
                    begBrace = tText.indexOf("[[");
                    curBrace = begBrace;
                }
            } else {
                begBrace = -1;  // This means there is no closing brace
            }
        }

        return tText.trim();
    }

    protected String extractSquareBraceText(String text) {
        String[] chunks = PlaceHelper.split(text, '|');
        if (chunks.length == 1) {
            return chunks[0];
        } else if (chunks.length == 2) {
            return chunks[1];
        } else {
            return "";
        }
    }

    protected String handleRefSections(String text) {
        String tText = text;
        StringBuilder buff = new StringBuilder();

        int ndx0 = tText.toUpperCase().indexOf("<REF");
        while (ndx0 >= 0) {
            int ndx1 = tText.indexOf("/>", ndx0);
            int ndx2 = tText.toUpperCase().indexOf("</REF>", ndx0);
            if (ndx1 >= 0  &&  (ndx2 == -1  ||  ndx1 < ndx2)) {
                buff.append((ndx0 == 0) ? "" : tText.substring(0, ndx0));
                tText = tText.substring(ndx1+2);
            } else if (ndx2 >= 0  &&  (ndx1 == -1  ||  ndx2 < ndx1)) {
                buff.append((ndx0 == 0) ? "" : tText.substring(0, ndx0));
                if (ndx2+6 > tText.length()) {
                    tText = "";
                } else {
                    tText = tText.substring(ndx2+6);
                }
            }

            ndx0 = tText.toUpperCase().indexOf("<REF");
        }
        buff.append(tText);

        return buff.toString();
    }

    protected String handleHttpCommentsSections(String text) {
        String tText = text;
        StringBuilder buff = new StringBuilder();

        int ndx0 = tText.toUpperCase().indexOf("<!--");
        while (ndx0 >= 0) {
            int ndx1 = tText.indexOf("-->", ndx0);
            buff.append((ndx0 == 0) ? "" : tText.substring(0, ndx0));
            if (ndx1+3 > tText.length()) {
                tText = "";
            } else {
                tText = tText.substring(ndx1+3);
            }

            ndx0 = tText.toUpperCase().indexOf("<!--");
        }
        buff.append(tText);

        return buff.toString();
    }

    protected String handleEmptyParenthesis(String text) {
        return text.replaceAll("\\(\\)", "");
    }

    protected String expandTemplate(String text) {
        String wikitextUrl = templateURL + "%7B%7B" + text.replace(' ', '_') + "%7D%7D";
        String wikitext = saxHandler.parseWikiSAX(wikitextUrl);
        return wikitext;
    }
}
