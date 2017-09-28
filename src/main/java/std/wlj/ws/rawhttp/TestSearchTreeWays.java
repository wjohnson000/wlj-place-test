package std.wlj.ws.rawhttp;

import java.net.URL;

import org.familysearch.standards.place.ws.model.RootModel;

public class TestSearchTreeWays {

    static String url01 = "http://beta.familysearch.org/int-std-ws-place/places/request?text=Texas";
    static String url02 = "https://beta.familysearch.org/int-std-ws-place/places/request?text=Texas";
    static String url03 = "http://ws.place.std.cmn.beta.us-east-1.test.fslocal.org/places/request?text=Texas";

    public static void main(String...args) throws Exception {
        HttpHelper.acceptType = "application/xml";
        HttpHelper.authId = "Bearer USYSD8D316A3B393EF2BB96F3198FF848C07_idses-refa06.a.fsglobal.net";

        search(url01);
        search(url02);
        search(url03);
    }

    static void search(String urlString) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("==========================================================================");
        URL url = new URL(urlString);
        RootModel model = HttpHelper.doGET(url);
        System.out.println("MODEL? " + (model != null));
        try { Thread.sleep(500L); } catch(Exception ex) { }
    }
}
