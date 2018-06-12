/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.util.PlaceHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


/**
 * @author wjohnson000
 *
 */
public class ParseWikiChineseRulers {

    static class DynastyMini {
        String name;
        String altName;
        String description;
        String altDescription;
        String startYr;
        String range;

        public String toString() {
            StringBuilder buff = new StringBuilder();
            buff.append(name).append(" [").append(description).append("] -> ").append(startYr);
            if (altDescription != null) {
                buff.append(" or ").append(altName).append(" [").append(altDescription).append("]");
            }
            return buff.toString();
        }
    }

    static class THead {
        int ndx;
        int span;

        public THead(int ndx, int span) {
            this.ndx = ndx;
            this.span = span;
        }
    }

    static class TDetail {
        int rowSpan;
        String value;

        public TDetail(int span, String value) {
            this.rowSpan = span;
            this.value = value;
        }

        public String toString() {
            return value + " [" + rowSpan + "]";
        }
    }

    static final String filePath = "C:/temp/chinese-monarchs.html";

    static boolean     newDynasty = true;
    static DynastyMini dynasty = null;

    static THead  reignNdx = null;
    static THead  eraNameNdx = null;
    static THead  otherNameNdx = null;
    static THead  commonNameNdx = null;
    static THead  regnalNameNdx = null;
    static THead  templeNameNdx = null;
    static THead  courtesyNameNdx = null;
    static THead  personalNameNdx = null;
    static THead  posthumousNameNdx = null;

    static List<String> results = new ArrayList<>();


    public static void main(String...args) {
        try {
            Document rulersDoc = Jsoup.parse(new File(filePath), "UTF-8");
            Elements bodyX = rulersDoc.getElementsByTag("body");
            if (bodyX.size() == 1) {
                Element body = bodyX.get(0);
                Elements allBody = body.children();
                allBody.stream().forEach(ParseWikiChineseRulers::handleGeneric);
            }
        } catch (IOException ex) {
            System.out.println("OOPS!! " + ex);
        }

        System.out.println("\n");
        results.forEach(System.out::println);

        ParseWikiChineseRulersXML.createXML(results);

        System.exit(0);
    }

    static void handleGeneric(Element element) {
        if (element.tagName().equalsIgnoreCase("h2")  ||  element.tagName().equalsIgnoreCase("h3")  ||  element.tagName().equalsIgnoreCase("h4")) {
            handleDynasty(element);
        } else if (element.tagName().equalsIgnoreCase("table")) {
            handleTable(element);
        } else {
            element.children().stream().forEach(ParseWikiChineseRulers::handleGeneric);
        }
    }

    static void handleDynasty(Element headerElement) {
        dynasty = null;

        reignNdx = null;
        eraNameNdx = null;
        otherNameNdx = null;
        commonNameNdx = null;
        regnalNameNdx = null;
        templeNameNdx = null;
        courtesyNameNdx = null;
        personalNameNdx = null;
        posthumousNameNdx = null;

        Elements spans = headerElement.select("span");
        Elements headline = spans.stream()
            .map(elem -> elem.getElementsByClass("mw-headline"))
            .filter(elems -> elems != null  &&  elems.size() > 0)
            .findFirst().orElse(null);

        if (headline != null) {
            newDynasty = true;
            dynasty = parseDynasty(headline.text());
        }
    }

    static DynastyMini parseDynasty(String rawDynasty) {
        int pos0, pos1, pos2, pos3;

        if (null == rawDynasty) {
            return null;
        } else if ("external links".equalsIgnoreCase(rawDynasty)) {
            return null;
        }

        String tName = rawDynasty.replaceAll("\\(Northern\\)", "");
        tName = tName.replaceAll(" \\(Southern\\)", "");
        tName = tName.replaceAll(" \\(Eastern\\)", "");
        tName = tName.replaceAll(" \\(Western\\)", "");
        tName = tName.replaceAll(" \\(Later\\)", "");
        tName = tName.replaceAll(" dynasty", "");
        int pos = tName.indexOf(" of ");
        if (pos > 0) {
            tName = tName.substring(pos+4);
        }

        DynastyMini dymi = new DynastyMini();
        pos0 = tName.indexOf(" or ");
        if (pos0 < 1) {
            dymi.altName = null;
            dymi.altDescription = null;
        } else {
            String prefix = tName.substring(0, pos0);
            pos1 = prefix.indexOf('(');
            if (pos1 < 1) {
                dymi.altDescription = prefix.trim();
            } else {
                dymi.altDescription = prefix.substring(0, pos1).trim();
                dymi.altName = prefix.substring(pos1+1).replace(')', ' ').trim();
            }
            
            tName = tName.substring(pos0+4).trim();
        }

        pos1 = tName.indexOf('(');
        pos2 = tName.indexOf('(', pos1+1);

        if (pos1 < 1) {
            dymi.description = tName.trim();
        } else {
            dymi.description = tName.substring(0, pos1).trim();
            if (pos2 < 1) {
                dymi.name = tName.substring(pos1+1).replace(')', ' ').trim();
            } else {
                dymi.name = tName.substring(pos1+1, pos2).replace(')', ' ').trim();
                pos3 = tName.indexOf(')', pos2);
                dymi.range= tName.substring(pos2+1, pos3).replace(')', ' ').trim();
                String[] years = PlaceHelper.split(dymi.range, '–');
                dymi.startYr = years[0].trim();
                if (dymi.startYr.startsWith("c. ")) {
                    dymi.startYr = dymi.startYr.substring(3);
                    if (dymi.range.contains("BC")) {
                        dymi.startYr = dymi.startYr + " BC";
                    }
                }
            }
        }

        return dymi;
    }

    static void handleTable(Element tableElement) {
        if (dynasty != null) {
            int numCol = setHeaderNdx(tableElement);
            if (numCol > 0) {
                setEmperorDetails(tableElement, numCol);
            }
        }
    }

    static int setHeaderNdx(Element tableElement) {
        Elements trs = tableElement.getElementsByTag("tr");
        Elements ths = trs.get(0).getElementsByTag("th");

        int ndx = 0;
        int colspan = 1;
        for (int i=0;  i<ths.size();  i++, ndx+= colspan) {
            Element th = ths.get(i);
            String head = th.text().toLowerCase();
            colspan = 1;
            String xxx = th.attr("colspan");
            if (xxx != null  &&  ! xxx.trim().isEmpty()) {
                colspan = Integer.parseInt(xxx.trim());
            }

            if (head.startsWith("\"sovereign\" or \"emperor\"")) {
                commonNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("name by which most commonly")) {
                commonNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("name most commonly")) {
                commonNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("personal name")) {
                personalNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("posthumous name")) {
                posthumousNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("regnal name")) {
                regnalNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("temple name")) {
                templeNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("courtesy name")) {
                courtesyNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("other name")) {
                otherNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("era names")) {
                eraNameNdx = new THead(ndx, colspan);
            } else if (head.startsWith("reign")) {
                reignNdx = new THead(ndx, colspan);
            }
        }

        return ndx;
    }

    static void setEmperorDetails(Element tableElement, int numCol) {
        Elements trs = tableElement.getElementsByTag("tr");
        TDetail[] row = new TDetail[numCol];
        for (int i=0;  i<trs.size();  i++) {
            Element tr = trs.get(i);
            row = getRow(tr, row);
            printEmperorDetails(row);
        }
    }

    static void printEmperorDetails(TDetail[] row) {
        StringBuilder buff = new StringBuilder();
        if (row == null  ||  row.length == 0  ||  row[0] == null) {
            return;
        }

        if (newDynasty) {
            newDynasty = false;
            buff.append(dynasty.name);
            buff.append("|").append(dynasty.description);
            buff.append("|").append(dynasty.altName == null ? "" : dynasty.altName);
            buff.append("|").append(dynasty.altDescription == null ? "" : dynasty.altDescription);
            buff.append("|").append(dynasty.startYr == null ? "" : dynasty.startYr);
        } else {
            buff.append("||||");
        }
        
        if (commonNameNdx == null  ||  commonNameNdx.ndx >= row.length) {
            buff.append("|");
        } else {
            buff.append("|").append(row[commonNameNdx.ndx].value);
        }

        if (posthumousNameNdx == null  ||  posthumousNameNdx.ndx+1 >= row.length) {
            buff.append("|");
        } else if (posthumousNameNdx.span == 1) {
            buff.append("|").append(row[posthumousNameNdx.ndx].value);
        } else {
            buff.append("|").append(row[posthumousNameNdx.ndx+1].value);
        }
        
        if (regnalNameNdx == null  ||  regnalNameNdx.ndx+1 >= row.length) {
            buff.append("|");
        } else if (regnalNameNdx.span == 1) {
            buff.append("|").append(row[regnalNameNdx.ndx].value);
        } else {
            buff.append("|").append(row[regnalNameNdx.ndx+1].value);
        }
        
        if (templeNameNdx == null  ||  templeNameNdx.ndx+1 >= row.length) {
            buff.append("|");
        } else if (templeNameNdx.span == 1) {
            buff.append("|").append(row[templeNameNdx.ndx].value);
        } else {
            buff.append("|").append(row[templeNameNdx.ndx+1].value);
        }

        if (courtesyNameNdx == null  ||  courtesyNameNdx.ndx+1 >= row.length) {
            buff.append("|");
        } else if (courtesyNameNdx.span == 1) {
            buff.append("|").append(row[courtesyNameNdx.ndx].value);
        } else {
            buff.append("|").append(row[courtesyNameNdx.ndx+1].value);
        }

        if (otherNameNdx == null  ||  otherNameNdx.ndx+1 >= row.length) {
            buff.append("|");
        } else if (otherNameNdx.span == 1) {
            buff.append("|").append(row[otherNameNdx.ndx].value);
        } else {
            buff.append("|").append(row[otherNameNdx.ndx+1].value);
        }

        if (personalNameNdx == null  ||  personalNameNdx.ndx+1 >= row.length) {
            buff.append("|");
        } else if (personalNameNdx.span == 1) {
            buff.append("|").append(row[personalNameNdx.ndx].value);
        } else {
            buff.append("|").append(row[personalNameNdx.ndx+1].value);
        }

        if (reignNdx == null) {
            buff.append("|");
        } else {
            buff.append("|").append(row[reignNdx.ndx].value);
        }

        if (eraNameNdx == null) {
            buff.append("||||");
        } else {
            for (int i=0;  i<4;  i++) {
                if (i >= eraNameNdx.span) {
                    buff.append("|");
                } else {
                    buff.append("|").append(row[eraNameNdx.ndx+i].value);
                }
            }
        }

        results.add(buff.toString());
    }

    static TDetail[] getRow(Element tr, TDetail[] row) {
        Elements tds = tr.getElementsByTag("td");
        if (tds == null  ||  tds.size() == 0) {
            return row;
        }

        TDetail[] newRow = row;

        int pos = 0;
        for (int i=0;  i<tds.size();  i++) {
            Element td = tds.get(i);
            String value = td.text();
            if (value.equals("-")  ||  value.equals("–")) {
                value = "";
            }

            int rowspan = 1;
            String rowsp = td.attr("rowspan");
            if (rowsp != null  &&  ! rowsp.trim().isEmpty()) {
                rowspan = Integer.parseInt(rowsp.trim());
            }

            int colspan = 1;
            String colsp = td.attr("colspan");
            if (colsp != null  &&  ! colsp.trim().isEmpty()) {
                colspan = Integer.parseInt(colsp.trim());
            }

            boolean findSlot = false;
            while (! findSlot) {
                if (newRow[pos] == null) {
                    while (colspan > 0) {
                        newRow[pos++] = new TDetail(rowspan, value);
                        colspan--;
                    }
                    findSlot = true;
                } else if (newRow[pos].rowSpan <= 1) {
                    while (colspan > 0) {
                        newRow[pos++] = new TDetail(rowspan, value);
                        colspan--;
                    }
                    findSlot = true;
                } else {
                    newRow[pos++].rowSpan--;
                }
            }
        }

        while (pos < newRow.length) {
            newRow[pos++].rowSpan--;
        }

        return newRow;
    }
}
