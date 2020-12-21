/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ParseCJK {

    private static class MiniDyn implements Comparable<MiniDyn> {
        String name;
        String enName;
        String locale;
        int    yearFrom;
        int    yearTo;
        List<MiniEmp> emperors = new ArrayList<>();

        public String toString() {
            return name + " [" + enName + "]: " + yearFrom + "-" + yearTo;
        }

        @Override
        public int compareTo(MiniDyn that) {
            int diff = this.yearFrom - that.yearFrom;
            if (diff == 0) {
                diff = this.name.compareTo(that.name);
            }
            return diff;
        }
    }

    private static class MiniEmp implements Comparable<MiniEmp> {
        String name;
        String locale;
        int    yearFrom;
        int    yearTo;
        List<MiniRgn> reigns = new ArrayList<>();

        public String toString() {
            return "E::" + name + ": " + yearFrom + "-" + yearTo;
        }

        @Override
        public int compareTo(MiniEmp that) {
            int diff = this.yearFrom - that.yearFrom;
            if (diff == 0) {
                diff = this.name.compareTo(that.name);
            }
            return diff;
        }
    }

    private static class MiniRgn implements Comparable<MiniRgn> {
        String name;
        String locale;
        int    yearFrom;
        int    yearTo;

        public String toString() {
            return "R::" + name + ": " + yearFrom + "-" + yearTo;
        }

        @Override
        public int compareTo(MiniRgn that) {
            int diff = this.yearFrom - that.yearFrom;
            if (diff == 0) {
                diff = this.name.compareTo(that.name);
            }
            return diff;
        }
    }

    private static final String CJK_CALENDAR_FILENAME = "/org/familysearch/standards/date/shared/imperial_ja.xml";

    public static void main(String... args) {
        ParseCJK engine = new ParseCJK();

        List<MiniDyn> dynasties = engine.getDynasties();
        List<String> results = engine.prettyIfy(dynasties);
        results.forEach(System.out::println);
        System.exit(0);
    }

    List<MiniDyn> getDynasties() {
        List<MiniDyn> dynasties = new ArrayList<>();

        try(Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(CJK_CALENDAR_FILENAME), StandardCharsets.UTF_8)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(reader);

            int     noDynCount = 1;
            boolean inDynasty  = false;
            boolean inEmperor  = false;
            boolean inReign    = false;
            boolean hasMore    = true;
            String  groupMeta  = "";
            String  groupLang  = "";
            String  wordMeta   = "";
            String  wordLang   = "";

            while (hasMore) {
                switch (parser.next()) {
                case XMLStreamConstants.END_DOCUMENT:
                    hasMore = false;
                    parser.close();
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    wordMeta = "";
                    if (parser.getLocalName().equals("word-group")) {
                        groupMeta = parser.getAttributeValue(null, "meta");
                        groupLang = parser.getAttributeValue(null, "lang");

                        String type = parser.getAttributeValue(null, "type");
                        inDynasty = ("dynasty".equals(type));
                        inEmperor = ("emperor".equals(type));
                        inReign = ("reign".equals(type));
                    } else if (parser.getLocalName().equals("word")) {
                        wordMeta = parser.getAttributeValue(null, "meta");
                        wordLang = parser.getAttributeValue(null, "lang");
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (parser.getLocalName().equals("word-group")) {
                        inDynasty = false;
                        inEmperor = false;
                        inReign = false;
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String chars = parser.getText();
                    if (wordMeta != null  &&  ! chars.trim().isEmpty()) {
                        if (inDynasty) {
                            String[] chunks = PlaceHelper.split(wordMeta, '|');
                            MiniDyn mdyn = new MiniDyn();
                            mdyn.name = chars;
                            mdyn.enName = chunks[0];
                            mdyn.locale = (wordLang == null) ? groupLang : wordLang;
                            mdyn.yearFrom = Integer.parseInt(chunks[1]);
                            mdyn.yearTo = Integer.parseInt(chunks[2]);
                            dynasties.add(mdyn);
                        } else if (inEmperor) {
                            String[] chunks = PlaceHelper.split(wordMeta, '|');
                            MiniEmp memp = new MiniEmp();
                            memp.name = chars;
                            memp.locale = wordLang;
                            memp.yearFrom = Integer.parseInt(chunks[1]);
                            memp.yearTo = Integer.parseInt(chunks[2]);

                            MiniDyn mdyn = findDynasty(dynasties, groupMeta);
                            if (mdyn == null) {
                                mdyn = new MiniDyn();
                                mdyn.name = "";
                                mdyn.locale = (wordLang == null) ? groupLang : wordLang;
                                mdyn.enName = "no-dynasty-" + noDynCount++;
                                mdyn.yearFrom = memp.yearFrom;
                                mdyn.yearTo = memp.yearTo;
                                dynasties.add(mdyn);
                            }
                            mdyn.emperors.add(memp);
                        } else if (inReign) {
                            String[] chunks = PlaceHelper.split(wordMeta, '|');
                            MiniRgn mrgn = new MiniRgn();
                            mrgn.name = chars;
                            mrgn.locale = wordLang;
                            mrgn.yearFrom = Integer.parseInt(chunks[1]);
                            mrgn.yearTo = Integer.parseInt(chunks[2]);

                            MiniDyn mdyn = findDynasty(dynasties, groupMeta);
                            if (mdyn == null) {
                                mdyn = findDynasty(dynasties, mrgn.yearFrom);
                            }

                            if (mdyn == null) {
                                System.out.println("NO-DYN" + mrgn + " --> " + mdyn);
                            } else {
                                MiniEmp memp = findEmperor(mdyn.emperors, mrgn.yearFrom);
                                if (memp == null) {
                                    System.out.println("NO-EMP" + mrgn + " --> " + mdyn);
                                } else {
                                    memp.reigns.add(mrgn);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        } catch (XMLStreamException | IOException e) {
            System.out.println("Oops ... " + e.getClass().getName() + " --> " + e.getMessage());
        }

        return dynasties;
    }

    MiniDyn findDynasty(List<MiniDyn> dynasties, String dynName) {
        return dynasties.stream()
                .filter(dyn -> dyn.enName.equalsIgnoreCase(dynName))
                .findFirst().orElse(null);
    }

    MiniDyn findDynasty(List<MiniDyn> dynasties, int year) {
        MiniDyn mdyn = null;

        int diff = Integer.MAX_VALUE;
        for (MiniDyn dynasty : dynasties) {
            int tDiff = year - dynasty.yearFrom;
            if (tDiff >= 0  &&  tDiff < diff) {
                mdyn = dynasty;
                diff = tDiff;
            }
        }

        return mdyn;
    }

    MiniEmp findEmperor(List<MiniEmp> emperors, int year) {
        MiniEmp memp = null;

        int diff = Integer.MAX_VALUE;
        for (MiniEmp emperor : emperors) {
            int tDiff = year - emperor.yearFrom;
            if (tDiff >= 0  &&  tDiff < diff) {
                memp = emperor;
                diff = tDiff;
            }
        }

        return memp;
    }

    List<String> prettyIfy(List<MiniDyn> dynasties) {
        List<String> stuff = new ArrayList<>(1000);

        Collections.sort(dynasties);
        for (MiniDyn dyn : dynasties) {
            boolean firstEmp = true;
            Collections.sort(dyn.emperors);

            for (MiniEmp emp : dyn.emperors) {
                boolean firstRgn = true;
                Collections.sort(emp.reigns);
                if (emp.reigns.isEmpty()) {
                    StringBuilder buff = new StringBuilder();
                    if (firstEmp) {
                        firstEmp = false;
                        buff.append(fixDynastyName(dyn.name)).append("|").append(dyn.locale).append("|").append(dyn.enName).append("|").append(dyn.yearFrom).append("|").append(dyn.yearTo);
                    } else {
                        buff.append("||||");
                    }
                    buff.append("|").append(emp.locale).append("|").append(emp.name).append("|").append(emp.yearFrom).append("|").append(emp.yearTo);
                    stuff.add(buff.toString());
                } else {
                    for (MiniRgn rgn : emp.reigns) {
                        StringBuilder buff = new StringBuilder();
                        if (firstEmp) {
                            firstEmp = false;
                            buff.append(dyn.locale).append("|").append(fixDynastyName(dyn.name)).append("|").append(dyn.enName).append("|").append(dyn.yearFrom).append("|").append(dyn.yearTo);
                        } else {
                            buff.append("||||");
                        }
                        if (firstRgn) {
                            firstRgn = false;
                            buff.append("|").append(emp.locale).append("|").append(emp.name).append("|").append(emp.yearFrom).append("|").append(emp.yearTo);
                        } else {
                            buff.append("||||");
                        }
                        buff.append("|").append(rgn.locale).append("|").append(rgn.name).append("|").append(rgn.yearFrom).append("|").append(rgn.yearTo);
                        stuff.add(buff.toString());
                    }
                }
            }
            stuff.add("");
        };

        return stuff;
    }

    String fixDynastyName(String dynastyName) {
        if (dynastyName == null) {
            return "";
        } else if (dynastyName.trim().startsWith("no-dyn")) {
            return dynastyName.trim().substring(0, dynastyName.length()-3);
        } else {
            return dynastyName.trim();
        }
    }
}
