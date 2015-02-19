package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class ExtXrefTypeGetAll {

    /** Base URL of the application */
//    private static String xrefationUrl = "http://localhost:8080/std-ws-place/places/xref-types";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places/xref-types";
    private static String baseUrl = "http://ec2-54-235-166-8.compute-1.amazonaws.com:8080/std-ws-place/places/xref-types";

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        System.out.println("---------------------------------------------------------------------------------");

        URL url = new URL(baseUrl);
        RootModel model = TestUtil.doGET(url);
        System.out.println("POST -- RM: " + model);

        for (TypeModel typeModel : model.getTypes()) {
            url = new URL(baseUrl + "/" + typeModel.getId());
            model = TestUtil.doGET(url);
            System.out.println("GET-ONE -- RM: " + model);
        }
    }
}
