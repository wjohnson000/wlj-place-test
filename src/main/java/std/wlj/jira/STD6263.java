package std.wlj.jira;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * The "<era>...</era>" tag is sometimes empty when doing interpretations ...
 * 
 * @author wjohnson000
 */
public class STD6263 {

    /** Base URL of the application */
    private static String baseUrl = "http://ws.date.std.cmn.prod.us-east-1.prod.fslocal.org/dates/interp?text=1776+july+01&accept-language=xxx";

    public static void main(String[] args) throws Exception {
        String xml  = HttpClientX.doGetXML(baseUrl);
        String json = HttpClientX.doGetJSON(baseUrl);

        System.out.println("\n\n\nXML\n: " + xml);
        System.out.println("\n\n\nJSON\n: " + json);
    }
}
