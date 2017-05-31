package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultModel;
import org.familysearch.standards.place.ws.model.RootModel;


public class TestSearchUSA {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/request?text=USA";
//    private static String baseUrl =   "https://familysearch.org/int-std-ws-place/places/request?limit=1000&pubType=pub_only&text=Dolinivka&threshold=0&valType=val_non_val&noCache=true";
//    private static String baseUrl = "http://ws.place.std.cmn.beta.us-east-1.test.fslocal.org/int-std-ws-place/places/request?text=Over+Stratton%2C+Somerset%2C+England%2C+United+Kingdom&limit=1";
    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places/request?text=Over+Stratton%2C+Somerset%2C+England%2C+United+Kingdom&limit=1";

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL(baseUrl);

        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM-count: " + model.getSearchResults().get(0).getCount());
        for (PlaceSearchResultModel placeModel : model.getSearchResults().get(0).getResults()) {
            PlaceRepresentationModel repModel = placeModel.getRep();
            System.out.println(
                    "PM: " + repModel.getId() +
                    " --> " + repModel.getFullDisplayName().getName() +
                    " " + getJurisdiction(repModel.getId(), repModel.getJurisdiction()) +
                    " --> " + repModel.getType().getCode());
        }
    }

    private static String getJurisdiction(int repId, JurisdictionModel jModel) {
        StringBuilder buff = new StringBuilder();
        buff.append("[").append(repId);
        JurisdictionModel tModel = jModel;
        while (tModel != null) {
            buff.append(",");
            buff.append(tModel.getId());
            tModel = tModel.getParent();
        }
        buff.append("]");
        return buff.toString();
    }
}
