package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestGetRepSolr {

    /** Base URL of the application */
    private static String masterUrl = "https://beta.familysearch.org/int-solr/places/select";

    /**
     * Run two tests ... a GET of a specific place-rep, and a search
     */
    public static void main(String[] args) throws Exception {
        getRep(1);
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(masterUrl + "?q=repId:" + repId + "&wt=json");
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }
}
