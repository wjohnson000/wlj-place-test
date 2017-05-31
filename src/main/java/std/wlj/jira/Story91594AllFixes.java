package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

/**
 * Two files with name data which needs to be fixed.  The first, "place-name-all.txt", has the
 * following format:
 * <ul>
 *   <li><strong>place-id</strong> -- place identifier, to which the name is tied</li>
 *   <li><strong>locale</strong> -- name locale</li>
 *   <li><strong>text</strong> -- name text</li>
 *   <li><strong>name-id</strong> -- primary key value</li>
 *   <li><strong>tran-id</strong> -- revision number</li>
 *   <li><strong>delete-flag</strong> -- "true" if the silly thing has been deleted</li>
 * </ul>
 * 
 * The second file, "display-name-all.txt", has the following format:
 * <ul>
 *   <li><strong>rep-id</strong> -- place-rep identifier, to which the name is tied</li>
 *   <li><strong>locale</strong> -- name locale</li>
 *   <li><strong>text</strong> -- name text</li>
 *   <li><strong>tran-id</strong> -- revision number</li>
 *   <li><strong>delete-flag</strong> -- "true" if the silly thing has been deleted</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Story91594AllFixes {

    static final String baseDir        = "D:/important";
    static final String fixedDir       = "fixed";
    static final String fixesFile      = "Character-Fixes-03.txt";

    static final String placeNameAll   = "place-name-all.txt";
    static final String placeNameFixes = "place-name-fixes-03.txt";

    static final String repNameAll     = "display-name-all.txt";
    static final String repNameFixes =   "display-name-fixes-03.txt";

    static class TheNameData {
        int     ownerId;    // place-id or rep-id
        String  locale;     // name locale
        int     dbId;       // database identifier
        String  text;       // name text
        String  textFixed;  // name text, fixed
        int     transxId;   // transaction-id, i.e., revision number
        boolean isDeleted;  // flag indicating if the name has been deleted
        boolean isInvalid;  // flag indicating if the name is invalid
    }

    static Map<String,String> charMapping = new HashMap<>();

    public static void main(String...args) throws IOException {
        setupCharMapping();
//        generatePlaceNameFixes();
        generateRepDisplayNameFixes();
    }

    static void setupCharMapping() throws IOException {
        List<String> nameData = Files.readAllLines(Paths.get(baseDir, fixesFile), Charset.forName("UTF-8"));
        for (String nameDatum : nameData) {
            String[] chunks  = nameDatum.split("\\t");
            String fromChars = "";
            String toChars   = "";

            if (chunks.length == 1) {
                fromChars = chunks[0]; 
            } else if (chunks.length == 2) {
                fromChars = chunks[0];
                toChars   = chunks[1];
            } else {
                System.out.println("What the what ... " + nameData);
                continue;
            }

            String fromStr = getString(fromChars);
            String toStr   = getString(toChars);
            if (fromStr.equals(toStr)) {
                System.out.println(">>" + fromStr + "<<  --  >>" + toStr + "<< -- same!!");
            } else {
                System.out.println(">>" + fromStr + "<<  --  >>" + toStr);
                charMapping.put(fromStr, toStr);
            }
        }
    }

    static void generatePlaceNameFixes() throws IOException {
        Map<Integer, TheNameData> nameMap = new HashMap<>();
        List<String> nameData = Files.readAllLines(Paths.get(baseDir, placeNameAll), Charset.forName("UTF-8"));
        System.out.println("Number of variant names: " + nameData.size());

        for (String nameDatum : nameData) {
            String[] chunks = nameDatum.split("\\|");
            if (chunks.length > 4) {
                String text      = chunks[2];
                String fixedText = fixText(text);
                boolean isInvalid = isInvalid(fixedText);

                if (! text.equals(fixedText)  ||  isInvalid) {
                    TheNameData tNameData = new TheNameData();
                    tNameData.ownerId   = Integer.parseInt(chunks[0]);
                    tNameData.locale    = chunks[1];
                    tNameData.dbId      = Integer.parseInt(chunks[3]);
                    tNameData.text      = text;
                    tNameData.textFixed = fixedText;
                    tNameData.transxId  = Integer.parseInt(chunks[4]);
                    tNameData.isDeleted = "true".equals(chunks[5]);
                    tNameData.isInvalid = isInvalid;

                    if (! tNameData.isDeleted) {
                        TheNameData xNameData = nameMap.get(tNameData.dbId);
                        if (xNameData == null  ||  xNameData.transxId < tNameData.transxId) {
                            nameMap.put(tNameData.dbId, tNameData);
                        }
                    }
                }
            }
        }

        System.out.println("Bad count: " + nameMap.size());
        List<String> outData = new ArrayList<>(nameMap.size());
        for (TheNameData tNameData : nameMap.values()) {
            StringBuilder buff = new StringBuilder();
            buff.append(tNameData.dbId);
            buff.append("|").append(tNameData.ownerId);
            buff.append("|").append(tNameData.locale);
            buff.append("|").append(tNameData.transxId);
            buff.append("|").append(tNameData.isDeleted);
            buff.append("|").append(tNameData.isInvalid);
            buff.append("|").append(tNameData.text);
            buff.append("|").append(tNameData.textFixed);
            buff.append("|").append(getUChars(tNameData.text));
            buff.append("|").append(getUChars(tNameData.textFixed));
            outData.add(buff.toString());
        }

        Files.write(Paths.get(baseDir, fixedDir, placeNameFixes), outData, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void generateRepDisplayNameFixes() throws IOException {
        Map<String, TheNameData> nameMap = new HashMap<>();
        List<String> nameData = Files.readAllLines(Paths.get(baseDir, "display-name-all.txt"), Charset.forName("UTF-8"));
        System.out.println("Number of display names: " + nameData.size());

        for (String nameDatum : nameData) {
            String[] chunks = nameDatum.split("\\|");
            if (chunks.length > 4) {
                String text      = chunks[2];
                String fixedText = fixText(text);
                boolean isInvalid = isInvalid(fixedText);

                if (! text.equals(fixedText)  ||  isInvalid) {
                    TheNameData tNameData = new TheNameData();
                    tNameData.ownerId   = Integer.parseInt(chunks[0]);
                    tNameData.locale    = chunks[1];
                    tNameData.dbId      = 0;
                    tNameData.text      = text;
                    tNameData.textFixed = fixedText;
                    tNameData.transxId  = Integer.parseInt(chunks[3]);
                    tNameData.isDeleted = "true".equals(chunks[4]);
                    tNameData.isInvalid = isInvalid;

                    if (! tNameData.isDeleted) {
                        String key = tNameData.ownerId + "." + tNameData.locale;
                        TheNameData xNameData = nameMap.get(key);
                        if (xNameData == null  ||  xNameData.transxId < tNameData.transxId) {
                            nameMap.put(key, tNameData);
                        }
                    }
                }
            }
        }

        System.out.println("Bad count: " + nameMap.size());
        List<String> outData = new ArrayList<>(nameMap.size());
        for (TheNameData tNameData : nameMap.values()) {
            StringBuilder buff = new StringBuilder();
            buff.append(tNameData.dbId);
            buff.append("|").append(tNameData.ownerId);
            buff.append("|").append(tNameData.locale);
            buff.append("|").append(tNameData.transxId);
            buff.append("|").append(tNameData.isDeleted);
            buff.append("|").append(tNameData.isInvalid);
            buff.append("|").append(tNameData.text);
            buff.append("|").append(tNameData.textFixed);
            buff.append("|").append(getUChars(tNameData.text));
            buff.append("|").append(getUChars(tNameData.textFixed));
            outData.add(buff.toString());
        }

        Files.write(Paths.get(baseDir, fixedDir, repNameFixes), outData, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String getString(String stuff) {
        StringBuilder buff = new StringBuilder();

        String[] chars = stuff.split(" ");
        for (String charU : chars) {
            if (charU.equalsIgnoreCase("X")) {
                return null;
            } else if (! charU.isEmpty()) {
                buff.append(getChar(charU));
            }
        }

        return buff.toString();
    }

    static char getChar(String uNumber) {
        char result = ' ';

        if (uNumber.trim().isEmpty()) {
            // do nothing
        } else if (uNumber.startsWith("U+")) {
            String uuNumber = uNumber.substring(2);
            int iChar = Integer.parseInt(uuNumber, 16);
            result = (char)iChar;
        } else  {
            System.out.println("Invalid ... " + uNumber);
        }

        return result;
    }

    static String fixText(String text) {
        String newText = text;

        for (Map.Entry<String, String> entry : charMapping.entrySet()) {
            String fromText = entry.getKey();
            String toText   = entry.getValue();
            if (toText == null) {
                continue;
            }

            int ndx = newText.indexOf(fromText);
            while (ndx >= 0) {
                String pre = "";
                String post = "";
                if (ndx == 0) {  // Text to be munged is at the beginning of the Name
                    post = newText.substring(fromText.length());
                } else if (ndx + fromText.length() >= newText.length()) {  // Text to be munged is at the end of the Name
                    pre = newText.substring(0, ndx);
                } else { // Text to be munged is in the middle of the Name
                    pre = newText.substring(0, ndx);
                    post = newText.substring(ndx + fromText.length());
                }
                newText = pre + toText + post;
                ndx = newText.indexOf(fromText);
            }
        }

        return newText;
    }

    static boolean isInvalid(String text) {
        for (Map.Entry<String, String> entry : charMapping.entrySet()) {
            String fromText = entry.getKey();
            String toText   = entry.getValue();

            if (toText == null) {
                int ndx = text.indexOf(fromText);
                if (ndx >= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    static String getUChars(String text) {
        return text.chars()
            .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
            .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
            .collect(Collectors.joining(" "));
    }
}
