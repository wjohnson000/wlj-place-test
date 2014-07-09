package std.wlj.ws.rawhttp.core;

import java.net.*;

import org.familysearch.standards.core.ws.model.RootModel;


public class TestLocaleAll {

    /** Base URL of the application */
    private static String baseUrl = "http://core-ws-dev.dev.fsglobal.org/std-ws-core/core";
//    private static String baseUrl = "http://localhost:8080/std-ws-core/core";


    /**
     * Run two tests ... a GET of a all types, and a GET of a specific type
     */
    public static void main(String[] args) throws Exception {
        readLocales("/locales");  // ID range = 1 - 993
        readLocales("/locales/en-US");  // ID range = 1 - 993
    }

    private static void readLocales(String subUrl) throws Exception {
        URL url = new URL(baseUrl + subUrl);
        long nnow = System.nanoTime();
        RootModel model = TestUtil.doGET(url);
        nnow = System.nanoTime() - nnow;
        System.out.println("RM: " + model);
        System.out.println(" T: " + (nnow / 1000000.0));
    }
}
