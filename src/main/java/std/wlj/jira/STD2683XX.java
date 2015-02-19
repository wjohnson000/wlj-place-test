package std.wlj.jira;

import java.net.*;
import std.wlj.ws.rawhttp.TestUtil;


public class STD2683XX {

    /** Delete three place-reps ... */
    private static String[] delUrls = {
        "http://localhost:8080/std-ws-place/places/reps/7095506?newRepId=7095507",
        "http://localhost:8080/std-ws-place/places/reps/7095509?newRepId=7095508",
        "http://localhost:8080/std-ws-place/places/reps/9129974?newRepId=7095508"
    };

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        for (String delUrl : delUrls) {
            URL url = new URL(delUrl);
            TestUtil.doDELETE(url);
        }
    }
}
