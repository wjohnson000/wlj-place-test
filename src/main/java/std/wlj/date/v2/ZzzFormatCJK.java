/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
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
public class ZzzFormatCJK {

    private static class MiniDyn implements Comparable<MiniDyn> {
        String name;
        String enName;
        int    year;
        List<MiniEmp> emperors = new ArrayList<>();

        public String toString() {
            return name + " [" + enName + "]: " + year;
        }

        @Override
        public int compareTo(MiniDyn that) {
            int diff = this.year - that.year;
            if (diff == 0) {
                diff = this.name.compareTo(that.name);
            }
            return diff;
        }
    }

    private static class MiniEmp implements Comparable<MiniEmp> {
        String name;
        int    year;
        List<MiniRgn> reigns = new ArrayList<>();

        public String toString() {
            return "E::" + name + ": " + year;
        }

        @Override
        public int compareTo(MiniEmp that) {
            int diff = this.year - that.year;
            if (diff == 0) {
                diff = this.name.compareTo(that.name);
            }
            return diff;
        }
    }

    private static class MiniRgn implements Comparable<MiniRgn> {
        String name;
        int    year;

        public String toString() {
            return "R::" + name + ": " + year;
        }

        @Override
        public int compareTo(MiniRgn that) {
            int diff = this.year - that.year;
            if (diff == 0) {
                diff = this.name.compareTo(that.name);
            }
            return diff;
        }
    }

//    private static final String CJK_CALENDAR_FILENAME = "/std/wlj/date/v2/imperial_zh.xml";
    private static final String CJK_CALENDAR_FILENAME = "/org/familysearch/standards/date/shared/imperial_zh.xml";

    public static void main(String... args) {
        ZzzFormatCJK engine = new ZzzFormatCJK();

        List<MiniDyn> dynasties = engine.getDynasties();
        List<String> results = engine.prettyIfy(dynasties);
        results.forEach(System.out::println);
        System.exit(0);
    }

    List<MiniDyn> getDynasties() {
        List<MiniDyn> dynasties = new ArrayList<>();

        try(Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(CJK_CALENDAR_FILENAME), Charset.forName("UTF-8"))) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(reader);

            int     noDynCount = 1;
            boolean inDynasty  = false;
            boolean inEmperor  = false;
            boolean inReign    = false;
            boolean hasMore    = true;
            String groupMeta   = "";
            String wordMeta    = "";

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

                        String type = parser.getAttributeValue(null, "type");
                        inDynasty = ("dynasty".equals(type));
                        inEmperor = ("emperor".equals(type));
                        inReign = ("reign".equals(type));
                    } else if (parser.getLocalName().equals("word")) {
                        wordMeta = parser.getAttributeValue(null, "meta");
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
                            mdyn.year = Integer.parseInt(chunks[1]);
                            dynasties.add(mdyn);
                        } else if (inEmperor) {
                            String[] chunks = PlaceHelper.split(wordMeta, '|');
                            MiniEmp memp = new MiniEmp();
                            memp.name = chars;
                            memp.year = Integer.parseInt(chunks[1]);

                            MiniDyn mdyn = findDynasty(dynasties, groupMeta);
                            if (mdyn == null) {
                                mdyn = new MiniDyn();
                                mdyn.name = "";
                                mdyn.enName = "no-dynasty-" + noDynCount++;
                                mdyn.year = memp.year;
                                dynasties.add(mdyn);
                            }
                            mdyn.emperors.add(memp);
                        } else if (inReign) {
                            String[] chunks = PlaceHelper.split(wordMeta, '|');
                            MiniRgn mrgn = new MiniRgn();
                            mrgn.name = chars;
                            mrgn.year = Integer.parseInt(chunks[1]);

                            MiniDyn mdyn = findDynasty(dynasties, groupMeta);
                            if (mdyn == null) {
                                mdyn = findDynasty(dynasties, mrgn.year);
                            }

                            if (mdyn == null) {
                                System.out.println("NO-DYN" + mrgn + " --> " + mdyn);
                            } else {
                                MiniEmp memp = findEmperor(mdyn.emperors, mrgn.year);
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
            int tDiff = year - dynasty.year;
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
            int tDiff = year - emperor.year;
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
                        buff.append(fixDynastyName(dyn.name)).append("|").append(dyn.enName).append("|").append(dyn.year);
                    } else {
                        buff.append("||");
                    }
                    buff.append("|").append(emp.name).append("|").append(emp.year);
                    stuff.add(buff.toString());
                } else {
                    for (MiniRgn rgn : emp.reigns) {
                        StringBuilder buff = new StringBuilder();
                        if (firstEmp) {
                            firstEmp = false;
                            buff.append(fixDynastyName(dyn.name)).append("|").append(dyn.enName).append("|").append(dyn.year);
                        } else {
                            buff.append("||");
                        }
                        if (firstRgn) {
                            firstRgn = false;
                            buff.append("|").append(emp.name).append("|").append(emp.year);
                        } else {
                            buff.append("||");
                        }
                        buff.append("|").append(rgn.name).append("|").append(rgn.year);
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
