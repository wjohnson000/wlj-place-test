/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.api.model.GenSimpleDate;
import org.familysearch.standards.date.common.Constants;
import org.familysearch.standards.date.common.MonthDictionary;


/**
 * @author wjohnson000
 *
 */
public class DMYGenSimpleDateLookup {

    public static final Pattern DMY_SIMPLE_DATE_PAT = Pattern.compile(
        "^(?:([0-9][0-9]?))[ \\+]" +
        "(?:(\\p{L}+))[ \\+]" +
        "(?:([12][0-9][0-9][0-9]))$"
    );

    private static final Dictionary monthDict = MonthDictionary.getMonthDictionary();

    public static boolean isValidDMY(String text) {
        return text != null  &&  DMY_SIMPLE_DATE_PAT.matcher(text.trim()).matches();
    }

    public static GenSimpleDate from(String simpleDMY) throws GenDateException {
        Matcher m = DMY_SIMPLE_DATE_PAT.matcher(simpleDMY.trim());

        if (m.matches()) {
            int day   = Integer.parseInt(m.group(1));
            String monName = m.group(2);
            List<Word> months = monthDict.findWords(monName);
            Word month = months.stream()
                    .filter(word -> word.getTypes().contains(Constants.TYPE_MONTH))
                    .findFirst().orElse(null);
            if (month != null) {
                String monNumX = month.getTypes().stream()
                        .filter(type -> ! "month".equals(type))
                        .findFirst().orElse("0");
                int monthNum = Integer.valueOf(monNumX);
                int year  = Integer.parseInt(m.group(3));
                return (monthNum == 0) ? null : new GenSimpleDate(false, year, monthNum, day, 0, 0, 0);
            }            
        }
        return null;
    }
}
