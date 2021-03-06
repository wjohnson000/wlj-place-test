package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestSearchPouilon {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        searchPouillon();
    }

    private static void searchPouillon() throws Exception {
        URL url = new URL(baseUrl + "/request?text=Pouillon&limit=1000&threshold=0&fuzzy=ED_80&pubType=pub_only");
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }
}
