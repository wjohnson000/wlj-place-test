/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.http;

import org.familysearch.standards.date.ws.model.Dates;
import org.familysearch.standards.date.util.DateModelWrapper;

import std.wlj.marshal.POJOMarshalUtil;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class InterpDate {

    private static String baseUrl = "http://ws.date.standards.service.integ.us-east-1.dev.fslocal.org/dates/interp";

    public static void main(String... args) {
        String dateResp = HttpClientX.doGetXML(baseUrl + "?text=20January1999");
        Dates dates = POJOMarshalUtil.fromXML(dateResp, Dates.class);
        System.out.println("Dates:\n" + dates);

        DateModelWrapper wrapper = new DateModelWrapper(dates);
    }
}
