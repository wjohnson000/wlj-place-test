/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.http;

import java.util.Collections;

import org.familysearch.standards.date.ws.model.Dates;

import std.wlj.marshal.POJOMarshalUtil;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class InterpDate {

//    private static String baseUrl = "http://ws.date.standards.service.integ.us-east-1.dev.fslocal.org/dates/interp";
    private static String baseUrl = "http://localhost:8080/std-ws-date/dates/interp";

    public static void main(String... args) {
        String dateResp = HttpClientX.doGetXML(baseUrl + "?text=20January1999", Collections.emptyMap());
        Dates dates = POJOMarshalUtil.fromXML(dateResp, Dates.class);
        System.out.println("Dates: " + dates.getCount());
        if (dates.getCount() > 0) {
            System.out.println(" Date: " + dates.getDates().get(0).getGedcomx());
        }
    }
}
