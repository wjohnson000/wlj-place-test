package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class CitationTypeList {

    /** Base URL of the application */
    private static String citationUrl = "http://localhost:8080/std-ws-place/places/citation-types";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL(citationUrl);
        RootModel model = TestUtil.doGET(url);
        System.out.println("GET-ALL -- RM: " + model);
    }
}
