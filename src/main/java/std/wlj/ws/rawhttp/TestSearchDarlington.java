package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestSearchDarlington {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        searchPouillon();
    }

    private static void searchPouillon() throws Exception {
//        URL url = new URL(baseUrl + "/request?text=Darlington, South Carolina&metrics=true&partial=true&reqDirParents=3313327");
//        URL url = new URL(baseUrl + "/request?text=Ålborg, Denmark");
//        URL url = new URL(baseUrl + "/request?text=Ålborg, Denmark&threshold=0&pubType=pub_only&valType=val_non");
//        RootModel model = TestUtil.doGET(url);
//        System.out.println("RM: " + model);

        URL urlx = new URL(baseUrl + "/request");
        RootModel modelx = TestUtil.doGET(urlx, "text", "Ålborg, Denmark", "pubType", "pub_only");
        System.out.println("RM: " + modelx);
    }
}
