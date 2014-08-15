package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestGetRepSolr {

    /** Base URL of the application */
    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places/reps";
    private static String masterUrl = "http://place-solr.dev.fsglobal.org/solr/places/select";


    /**
     * Run two tests ... a GET of a specific place-rep, and a search
     */
    public static void main(String[] args) throws Exception {
        getRep(393288);
        getRep(393289);
        getRep(393290);
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(masterUrl + "?q=id:" + repId + "-*&wt=json");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);
    }
}
