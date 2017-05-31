package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultModel;
import org.familysearch.standards.place.ws.model.RootModel;


public class TestSearchGeneric {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/solr/places";
//  private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places";
//      private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places";

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

        String[][] searchStuff = {
            { "reqParents", "351", "reqTypes", "376,186", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
            { "reqParents", "351", "reqTypes", "186,376", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
//            { "reqParents", "351", "reqTypes", "186", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
//            { "reqParents", "351", "reqTypes", "376", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
//            { "reqParents", "351", "reqTypes", "376,186", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
//            { "reqParents", "351", "reqTypes", "186,376", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
//            { "reqParents", "351", "reqTypes", "186", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
//            { "reqParents", "351", "reqTypes", "376", "limit", "1000", "pubType", "pub_only", "valType", "val_non_val" },
        };

        for (String[] search : searchStuff) {
            URL urlx = new URL(baseUrl + "/request");
            RootModel modelx = HttpHelper.doGET(urlx, search);
            System.out.println("RM-count: " + modelx.getSearchResults().get(0).getCount());
            for (PlaceSearchResultModel placeModel : modelx.getSearchResults().get(0).getResults()) {
                PlaceRepresentationModel repModel = placeModel.getRep();
                System.out.println(
                    "PM: " + repModel.getFullDisplayName().getName() +
                    " " + getJurisdiction(repModel.getId(), repModel.getJurisdiction()) +
                    " --> " + repModel.getType().getCode());
            }
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
