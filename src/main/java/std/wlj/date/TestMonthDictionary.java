/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.DictionaryFactory;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.exception.GenDateException;
//import org.familysearch.standards.date.shared.CacheDictionaryImpl;
import org.familysearch.standards.date.shared.MonthDictionary;

/**
 * @author wjohnson000
 *
 */
public class TestMonthDictionary {

    static final Random random = new Random();

    static final StdLocale[] locales = {
        StdLocale.CHINESE,
        StdLocale.DANISH,
        StdLocale.ENGLISH,
        StdLocale.FRENCH,
        StdLocale.GERMAN,
        StdLocale.JAPANESE,
        StdLocale.KOREAN,
        StdLocale.POLISH,
        StdLocale.PORTUGUESE,
        StdLocale.SPANISH
    };

    public static void main(String... args) throws GenDateException {
        Dictionary mDictOld = DictionaryFactory.createEmptyDictionary();
        mDictOld.mergeDictionary(MonthDictionary.getMonthDictionary());
//        Dictionary mDictNew = new CacheDictionaryImpl();
//        mDictNew.mergeDictionary(MonthDictionary.getMonthDictionary());

        long time0, time1;
        long timeOld = 0L;
        long timeNew = 0L;

        for (int i=1;  i<10_000;  i++) {
            int mo = random.nextInt(12) + 1;
            String month = (mo < 10 ? "0" : "") + mo;
            StdLocale locale = locales[random.nextInt(locales.length)];

            time0 = System.nanoTime();
            List<Word> oldList = mDictOld.findWords(null, month, locale.toString());
            time1 = System.nanoTime();
            long timeOOO = time1 - time0;
            timeOld += timeOOO;

//            time0 = System.nanoTime();
//            List<Word> newList = mDictNew.findWords(null, month, locale.toString());
//            time1 = System.nanoTime();
//            long timeNNN = time1 - time0;
//            timeNew += timeNNN;
//
//            compareWords(oldList, newList);
        }

        System.out.println("OLD-time: " + timeOld / 1_000_000.0);
        System.out.println("NEW-time: " + timeNew / 1_000_000.0);
    }

    static void compareWords(List<Word> oldList, List<Word> newList) {
        if (oldList == null  &&  newList == null) {
            ;  // Do nothing
        } else if (oldList == null) {
            System.out.println("  ... oldList is null ...");
        } else if (newList == null) {
            System.out.println("  ... newList is null ...");
        } else if (oldList.size() != newList.size()) {
            System.out.println("  ... oldList size different than newList size ...");
        } else {
            Set<String> oldDetail = oldList.stream().map(Word::toString).collect(Collectors.toSet());
            Set<String> newDetail = newList.stream().map(Word::toString).collect(Collectors.toSet());
            if (oldDetail.containsAll(newDetail)) {
                System.out.println("  ... oldList and newList are the same ...");
            } else {
                System.out.println("  ... oldList and newList have different results ...");
            }
        }
    }
}
