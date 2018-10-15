/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

import org.familysearch.standards.DateModifier;
import org.familysearch.standards.GenealogicalDate;
import org.familysearch.standards.date.CalendarFactory;
import org.familysearch.standards.date.CalendarType;
import org.familysearch.standards.date.ContiguousDate;
import org.familysearch.standards.date.DMY;
import org.familysearch.standards.date.DateDetail;
import org.familysearch.standards.date.DateInterpretation;
import org.familysearch.standards.date.DateRange;
import org.familysearch.standards.date.DisjunctiveDate;
import org.familysearch.standards.date.GenDate;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.GenRangeDate;
import org.familysearch.standards.date.GenSimpleDate;
import org.familysearch.standards.date.GenealogicalDateImpl;
import org.familysearch.standards.date.SimpleDate;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.exception.GenDateParseException;
import org.familysearch.standards.date.parser.GenDateToken;
import org.familysearch.standards.date.shared.SharedUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author nayrb
 *         Date: 10/31/17
 */
public class DateV1Shim {
    private static Pattern DISJUNCTIVE_NORMAL_SPLITTER = Pattern.compile("\\s\\/\\s");

    public static List<GenealogicalDate> convertToList(GenealogicalDateImpl date) {
        List<GenealogicalDate> retval = new ArrayList<>();
        DateInterpretation interp = date.getInterpretation();
        DateDetail detail = interp.getDateDetail();
        if (null != detail.asSimple()) {
            retval.add(date);
        } else if (null != detail.asRange()) {
            //we don't need to convert this to a list as of yet
            retval.add(date);
        } else if (null != detail.asDisjunctive()) {
            String[] normalText = DISJUNCTIVE_NORMAL_SPLITTER.split(date.getNormalText());
            DisjunctiveDate disjunctiveDate = detail.asDisjunctive();
            assert disjunctiveDate.getDates().length == normalText.length;
            for (int i = 0; i < normalText.length; i++) {
                retval.add(
                        new GenealogicalDateImpl(
                                normalText[i],
                                String.valueOf(SimpleDate.getInstance(disjunctiveDate.getDates()[i].getEarliest(), disjunctiveDate.getDates()[i].getLatest(), disjunctiveDate.getDates()[i].getModifier())),
                                disjunctiveDate.getDates()[i].getModifier(),
                                date.isFullyNormalized())
                        );
            }
        }

        return retval;
    }


    public static List<GenDateInterpResult> interpDate(String originalText) throws GenDateException {
        List<GenDateInterpResult>   interps = new ArrayList<>();
        List<GenDate>               dates = new ArrayList<>();

        GenealogicalDate gd = GenealogicalDate.getInstance(originalText);

        if (null == gd) {
            throw new GenDateParseException(originalText + " is not able to be interpreted by the Date 1.0 GenealogicalDate interpreter.");
        }
        DateInterpretation interp = ((GenealogicalDateImpl) gd).getInterpretation();
        if (null == interp) {
            throw new GenDateParseException(originalText + " is not able to be interpreted by the Date 1.0 DateInterpretation interpreter.");
        }
        DateDetail detail = interp.getDateDetail();
        if (null == detail) {
            throw new GenDateParseException(originalText + " is not able to be interpreted by the Date 1.0 DateInterpretation interpreter. No detail.");
        }

        SimpleDate simple = detail.asSimple();
        DateRange range = detail.asRange();
        DisjunctiveDate disjunctive = detail.asDisjunctive();
        List<GenealogicalDate> gdList = DateV1Shim.convertToList((GenealogicalDateImpl) gd);
        if (null != simple) {
            dates = fromSimple(gdList);
        } else if (null != range) {
            dates = fromRange(gdList);
        } else if (null != disjunctive) {
            dates = fromDisjunctive(gdList);
        }
        for (int i = 0; i < dates.size(); i++) {
            GenDate               date;
            GenealogicalDate      oldDate = null;
            GenDateInterpResult   result;

            date = dates.get(i);
            if (gdList.size() > i) {
                oldDate = gdList.get(i);
            }
            result = new GenDateInterpResult(originalText, null, date);
            if (oldDate != null) {
                result.setAttr(SharedUtil.ATTR_V1_FULLY_NORMALIZED, oldDate.isFullyNormalized());
            }
            interps.add(result);
        }

        return interps;
    }


    protected static List<GenDate> fromSimple(List<GenealogicalDate> gdList) throws GenDateException {

        List<GenDate> retVal = new ArrayList<>(1);
        SimpleDate simple = gdList.get(0).getSimpleDates()[0];

        if (isBefore(simple)) {
            // create an open lower range
            retVal.add(createRange(isApproximate(simple), null, simple));
        } else if (isAfter(simple)) {
            // create an open upper range
            retVal.add(createRange(isApproximate(simple), simple, null));
        } else {
            retVal.add(createSimpleFromOldSimpleDate(isApproximate(simple), simple));
        }
        return retVal;
    }

    protected static List<GenDate> fromRange(List<GenealogicalDate> gdList) throws GenDateException {
        List<GenDate> retval = new ArrayList<>(1);
        DateRange range = ((GenealogicalDateImpl) gdList.get(0)).getInterpretation().getDateDetail().asRange();

        if ((range.getEarliestDay() instanceof SimpleDate) && (range.getLatestDay() instanceof SimpleDate)) {
            retval.add(createRange(isApproximate(range), range.getEarliestDay(), range.getLatestDay()));
            return retval;
        }
        throw new GenDateParseException("Date Range isn't composed of two simple dates");
    }

    protected static List<GenDate> fromDisjunctive(List<GenealogicalDate> gdList) throws GenDateException {

        int size = gdList.size();
        List<GenDate> dates = new ArrayList<>(size);
        for (GenealogicalDate d : gdList) {
            // this createSimple() call should create an array of exactly length 1
            //todo can we calculate isApproximate from the data in hand?
            dates.add(createSimpleFromOldSimpleDate(false, d.getSimpleDates()[0]));
        }
        return dates;
    }


    public static boolean isBC(GenDateToken token) {
        return token.hasType(GenDateToken.Type.BC);
    }

    protected static boolean isBefore(ContiguousDate contiguous) {
        switch (null != contiguous.getModifier() ? contiguous.getModifier() : DateModifier.CENSUS) {
        case TO:
        case BEFORE:
            return true;
        default:
            return false;
        }
    }

    protected static boolean isAfter(ContiguousDate contiguous) {
        switch (null != contiguous.getModifier() ? contiguous.getModifier() : DateModifier.CENSUS) {
        case FROM:
        case AFTER:
            return true;
        default:
            return false;
        }
    }

    protected static boolean isApproximate(ContiguousDate contiguous) {
        switch (null != contiguous.getModifier() ? contiguous.getModifier() : DateModifier.CENSUS) {
        case ABOUT:
        case CALCULATED:
        case ESTIMATED:
            return true;
        default:
            return false;
        }
    }

    protected static GenSimpleDate createSimpleFromOldSimpleDate(boolean isApproximate, SimpleDate simpleDate) throws GenDateException {

        DMY dmy = CalendarFactory.getCalendar(CalendarType.WESTERN).dmyFromDay(simpleDate.getDay());
        int y = dmy.getYear() < 0 ? dmy.getYear() + 1 : dmy.getYear();
        int m = (simpleDate.missingMonth()) ? 0 : dmy.getMonth();
        int d = (simpleDate.missingDay()) ? 0 : dmy.getDay();

        return new GenSimpleDate(isApproximate, y, m, d, 0, 0, 0);

    }

    protected static GenRangeDate createRange(boolean isApproximate, ContiguousDate earliest, ContiguousDate latest) throws GenDateException {
        // assume the earliest and latest are really SimpleDate objects

        GenSimpleDate early = null == earliest ? null : createSimpleFromOldSimpleDate(isApproximate, (SimpleDate) earliest);
        GenSimpleDate late = null == latest ? null : createSimpleFromOldSimpleDate(isApproximate, (SimpleDate) latest);

        return new GenRangeDate(isApproximate, early, late);
    }
}