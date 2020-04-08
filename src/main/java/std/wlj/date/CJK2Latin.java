/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.common.DateAPIConstants;
import org.familysearch.standards.date.common.ImperialDictionary;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class CJK2Latin {

    static final String TYPE_DYNASTY = "dynasty";
    static final String TYPE_EMPEROR = "emperor";
    static final String TYPE_REIGN   = "reign";

    static final Comparator<Thing> THINGER = (th1, th2) -> {
        if (th1.startYrInt != th2.startYrInt) {
            return th1.startYrInt - th2.startYrInt;
        } else {
            return th1.endYrInt - th2.endYrInt;
        }
    };

    static class Thing {
        Word   word;
        String type;  // "dynasty", "emperor", "reign"
        String id;
        String name;
        String nameLatin = "";
        String parentName;  // "emperor" --> "dynasty" and "reign" --> "emperor"
        String startYr;
        int    startYrInt;
        String endYr;
        int    endYrInt;
        String meta;
    }

    static Dictionary cjkDict = ImperialDictionary.getImperialDictionary();

    static List<Word> allDynWords;
    static List<Word> allEmpWords;
    static List<Word> allRgnWords;

    public static void main(String...args) {
        allDynWords = cjkDict.getWordsByType(DateAPIConstants.TYPE_DYNASTY);
        allEmpWords = cjkDict.getWordsByType(DateAPIConstants.TYPE_EMPEROR);
        allRgnWords = cjkDict.getWordsByType(DateAPIConstants.TYPE_REIGN);

        processLang("zh");
        processLang("ja");
        processLang("ko");

        System.exit(0);
    }

    static void processLang(String lang) {
        List<Thing> dynThings = allDynWords.stream()
                    .filter(wd -> wd.getLanguage().toString().contains(lang))
                    .map(th -> makeThing(TYPE_DYNASTY, th))
                    .collect(Collectors.toList());
        List<Thing> empThings = allEmpWords.stream()
                    .filter(wd -> wd.getLanguage().toString().contains(lang))
                    .map(th -> makeThing(TYPE_EMPEROR, th))
                    .collect(Collectors.toList());
        List<Thing> rgnThings = allRgnWords.stream()
                    .filter(wd -> wd.getLanguage().toString().contains(lang))
                    .map(th -> makeThing(TYPE_REIGN, th))
                    .collect(Collectors.toList());

        Collections.sort(dynThings, THINGER);
        Collections.sort(empThings, THINGER);
        Collections.sort(rgnThings, THINGER);

        Map<String, String> dynDups = getDuplicateName(dynThings);
        Map<String, String> empDups = getDuplicateName(empThings);
        Map<String, String> rgnDups = getDuplicateName(rgnThings);

        Set<String> dynDone = new HashSet<>();
        Set<String> empDone = new HashSet<>();
        Set<String> rgnDone = new HashSet<>();

        System.out.println("\n\n\nLANG: " + lang);
        for (Thing dyn : dynThings) {
            if (dynDone.contains(dyn.id)) {
                continue;
            }
            dynDone.add(dyn.id);

            System.out.println("\n\n");
            System.out.println(textify(dyn, dynDups));
            for (Thing emp : empThings) {
                if (emp.parentName.equals(dyn.id)  &&  ! empDone.contains(emp.id)) {
                    empDone.add(emp.id);
                    System.out.println();
                    System.out.println(textify(emp, empDups));
                    for (Thing rgn : rgnThings) {
                        if (rgn.parentName.equals(emp.id)  &&  ! rgnDone.contains(rgn.id)) {
                            rgnDone.add(rgn.id);
                            System.out.println(textify(rgn, rgnDups));
                        }
                    }
                }
            }
        }
    }

    protected static Map<String, String> getDuplicateName(List<Thing> things) {
        return things.stream()
                  .collect(Collectors.groupingBy(
                           dd -> dd.id,
                           HashMap::new,
                           Collectors.mapping(dd -> dd.name, Collectors.joining(", "))));
    }

    static Thing makeThing(String type, Word word) {
        Set<String> types = new HashSet<>();
        if (word.getTypes() != null) {
         types.addAll(word.getTypes());   
        }
        types.remove(type);

        Thing thing = new Thing();
        thing.word = word;
        thing.type = type;
        thing.meta = word.getMetadata();

        thing.id = metaPart(word.getMetadata(), 0);
        thing.name = word.getText();
        if (! types.isEmpty()) {
            thing.parentName = (new ArrayList<>(types)).get(0);
        }

        thing.startYr = metaPart(word.getMetadata(), 1);
        thing.startYrInt = makeInt(thing.startYr);
        thing.endYr = metaPart(word.getMetadata(), 2);
        thing.endYrInt = makeInt(thing.endYr);

        String lName = thing.id;
        char ch = lName.charAt(lName.length()-1);
        while (ch >= '0'  &&  ch <= '9') {
            lName = lName.substring(0, lName.length()-1);
            ch = lName.charAt(lName.length()-1);
        }
        ch = lName.charAt(lName.length()-1);
        if (ch == '-') {
            lName = lName.substring(0, lName.length()-1);
        }
        thing.nameLatin = lName;

        return thing;
    }

    static String metaPart(String metadata, int ndx) {
        return metadata.split("\\|")[ndx];
    }

    static int makeInt(String text) {
        Integer intx = PlaceHelper.parseInteger(text);
        return (intx == null) ? Integer.MAX_VALUE : intx.intValue();
    }

    static String textify(Thing thing, Map<String, String> dups) {
        
        StringBuilder buff = new StringBuilder();

        if (thing.type == TYPE_DYNASTY) {
            buff.append(thing.name).append("||");
        } else if (thing.type == TYPE_EMPEROR) {
            buff.append("|").append(thing.name).append("|");
        } else if (thing.type == TYPE_REIGN) {
            buff.append("||").append(thing.name);
        }

        buff.append("|").append(thing.id);
        buff.append("|").append(dups.getOrDefault(thing.id, thing.name));
        buff.append("|").append(thing.nameLatin);
        buff.append("|");
        buff.append("|").append(thing.startYrInt);
        buff.append("|").append(thing.endYrInt);

        return buff.toString();
    }
}
