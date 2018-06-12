/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.util.PlaceHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 * @author wjohnson000
 *
 */
public class ZzzAddEndDates01 {

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

    private static final String filePath = "C:/temp/chinese-monarchs.html";
    private static String IMPERIAL_ZH_FILE = "/org/familysearch/standards/date/shared/imperial_zh.xml";


    static THead  reignNdx = null;
    static THead  eraNameNdx = null;
    static THead  otherNameNdx = null;
    static THead  commonNameNdx = null;
    static THead  regnalNameNdx = null;
    static THead  templeNameNdx = null;
    static THead  courtesyNameNdx = null;
    static THead  personalNameNdx = null;
    static THead  posthumousNameNdx = null;

    static Map<String, String> empData = new HashMap<>();
    static Map<String, String> rgnData = new HashMap<>();

    public static void main(String...args) throws Exception {
        try {
            Document rulersDoc = Jsoup.parse(new File(filePath), "UTF-8");
            Elements bodyX = rulersDoc.getElementsByTag("body");
            if (bodyX.size() == 1) {
                Element body = bodyX.get(0);
                Elements allBody = body.children();
                allBody.stream().forEach(ZzzAddEndDates01::handleGeneric);
            }
            findMatches();
        } catch (IOException ex) {
            System.out.println("OOPS!! " + ex);
        }

        System.exit(0);
    }

    static void findMatches() throws Exception {
        URL url = ZzzAddEndDates01.class.getResource(IMPERIAL_ZH_FILE);
        List<String> data = Files.readAllLines(Paths.get(url.toURI()), Charset.forName("UTF-8"));
        for (String datum : data) {
            String line = datum;
            if (datum.trim().startsWith("<word ")) {
                int pos01 = datum.indexOf("meta");
                if (pos01 > 0) {
                    int pos02 = datum.indexOf(">", pos01);
                    int pos03 = datum.indexOf("<", pos02);
                    String meta = datum.substring(pos01+6, pos02-1);
                    String valu = datum.substring(pos02+1, pos03);
                    String year = PlaceHelper.split(meta, '|')[1];
                    String key = valu + "." + year;
                    String eeee = empData.getOrDefault(key, rgnData.get(key));
                    if (eeee != null) {
                        line = datum.substring(0, pos01) + " meta=\"" + meta + "|" + eeee + "\">" + valu + "</word>";
                    }
                }
            }

            System.out.println(line);
        }
    }

    static void handleGeneric(Element element) {
        if (element.tagName().equalsIgnoreCase("h2")  ||  element.tagName().equalsIgnoreCase("h3")  ||  element.tagName().equalsIgnoreCase("h4")) {
            handleDynasty(element);
        } else if (element.tagName().equalsIgnoreCase("table")) {
            handleTable(element);
        } else {
            element.children().stream().forEach(ZzzAddEndDates01::handleGeneric);
        }
    }

    static void handleDynasty(Element headerElement) {
        reignNdx = null;
        eraNameNdx = null;
        otherNameNdx = null;
        commonNameNdx = null;
        regnalNameNdx = null;
        templeNameNdx = null;
        courtesyNameNdx = null;
        personalNameNdx = null;
        posthumousNameNdx = null;
        
    }

    static void handleTable(Element tableElement) {
        int numCol = setHeaderNdx(tableElement);
        if (numCol > 0) {
            setEmperorDetails(tableElement, numCol);
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
            processEmperorAndReignDetails(row);
        }
    }

    static void processEmperorAndReignDetails(TDetail[] row) {
        if (row == null  ||  row.length == 0  ||  row[0] == null) {
            return;
        }

        if (reignNdx != null) {
            String years = row[reignNdx.ndx].value;
            if (commonNameNdx != null) {
                String name = row[commonNameNdx.ndx + commonNameNdx.span - 1].value;
                doEmpDetails(name, years);
            }
            if (posthumousNameNdx != null) {
                String name = row[posthumousNameNdx.ndx + posthumousNameNdx.span - 1].value;
                doEmpDetails(name, years);
            }
            if (regnalNameNdx != null) {
                String name = row[regnalNameNdx.ndx + regnalNameNdx.span - 1].value;
                doEmpDetails(name, years);
            }
            if (templeNameNdx != null) {
                String name = row[templeNameNdx.ndx + templeNameNdx.span - 1].value;
                doEmpDetails(name, years);
            }
            if (courtesyNameNdx != null) {
                String name = row[courtesyNameNdx.ndx + courtesyNameNdx.span - 1].value;
                doEmpDetails(name, years);
            }
            if (otherNameNdx != null) {
                String name = row[otherNameNdx.ndx + otherNameNdx.span - 1].value;
                doEmpDetails(name, years);
            }
            if (personalNameNdx != null) {
                String name = row[personalNameNdx.ndx + personalNameNdx.span - 1].value;
                doEmpDetails(name, years);
            }
        }

        if (eraNameNdx != null) {
            String name  = row[eraNameNdx.ndx+1].value;
            String years = row[eraNameNdx.ndx + eraNameNdx.span - 1].value;
            doRgnDetails(name, years);
        }
    }

    static void doEmpDetails(String name, String years) {
        Integer start = getStart(years);
        if (start != null) {
            Integer end = getEnd(years);
            if (end == null) {
                end = start;
            }
            empData.put(name+"."+start, String.valueOf(end));
        }
    }

    static void doRgnDetails(String name, String years) {
        Integer start = getStart(years);
        if (start != null) {
            Integer end = getEnd(years);
            if (end == null) {
                end = start;
            }
            rgnData.put(name+"."+start, String.valueOf(end));
        }
    }
    
    private static Integer getStart(String years) {
        boolean isBC = false;
        String[] yrRange = PlaceHelper.split(years, '–');
        String yrString = yrRange[0];
        try {
            yrString = yrString.replaceAll("AD", "");
            if (yrString.contains("BC")) {
                isBC = true;
                yrString = yrString.replaceAll("BC1", "");
                yrString = yrString.replaceAll("BC", "");
            } else if (years.trim().endsWith("BC1")  ||  years.trim().endsWith("BC")) {
                isBC = true;
            }
            return (isBC ? -1 : 1) * Integer.parseInt(yrString.trim());
        } catch(NumberFormatException ex) {
            return null;
        }
    }
    
    private static Integer getEnd(String years) {
        boolean isBC = false;
        String[] yrRange = PlaceHelper.split(years, '–');
        String yrString = yrRange[yrRange.length-1];
        try {
            yrString = yrString.replaceAll("AD", "");
            if (yrString.contains("BC")) {
                isBC = true;
                yrString = yrString.replaceAll("BC1", "");
                yrString = yrString.replaceAll("BC", "");
            }
            return (isBC ? -1 : 1) * Integer.parseInt(yrString.trim());
        } catch(NumberFormatException ex) {
            return null;
        }
    }

    static TDetail[] getRow(Element tr, TDetail[] row) {
        Elements tds = tr.getElementsByTag("td");
        if (tds == null  ||  tds.size() == 0) {
            return row;
        } else if (String.valueOf(tr).length() > 1000) {
            return row;
        }

        TDetail[] newRow = row;

        int pos = 0;
        for (int i=0;  i<tds.size();  i++) {
            Element td = tds.get(i);

            String value = td.text();
            Elements sup = td.getElementsByTag("sup");
            if (sup != null  &&  sup.size() > 0) {
                if (td.textNodes() != null  &&  td.textNodes().size() > 0) {
                    value = td.textNodes().get(0).getWholeText();
                }
            }
            
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
