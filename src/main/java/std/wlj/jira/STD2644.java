package std.wlj.jira;

import java.net.*;
import java.util.List;

import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultsModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.VariantModel;

import std.wlj.ws.rawhttp.TestUtil;


/**
 * Do some or all of the following:
 * <br>-- read a place
 * <br>-- update a place
 * 
 * @author wjohnson000
 */
public class STD2644 {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places";

    public static void main(String[] args) throws Exception {
        String[] textes = {
            "Северная, Аромашевский район, Тюменская область, Россия",
            "Severnaya, Aromashevskiy Rayon, Tyumen Oblast, Russia"
        };

        for (String text : textes) {
            URL url = new URL(baseUrl + "/request?text=" + text + "&fuzzy=ED&&threshold=0&pubType=pub_only&valType=val_non");
            RootModel model = TestUtil.doGET(url);
//            System.out.println("RM: " + model);
            for (PlaceSearchResultsModel results : model.getSearchResults()) {
                for (PlaceSearchResultModel result : results.getResults()) {
                    printIt("RESULT ...", result.getRep());
                }
            }
        }
    }

    private static void printIt(String header, PlaceRepresentationModel repModel) {
        System.out.println("\n" + header);
        System.out.println("  ID: " + repModel.getId());
        System.out.println("  FN: " + repModel.getFullDisplayName().getName());
        System.out.println("  OW: " + repModel.getOwnerId());

        System.out.print("  JR: ");
        JurisdictionModel jurisModel = repModel.getJurisdiction();
        while (jurisModel != null) {
            System.out.print(jurisModel.getId() + ":" + jurisModel.getName() + "  <--> ");
            jurisModel = jurisModel.getParent();
        }
        System.out.println();

        if (repModel.getDisplayNames() == null) {
            System.out.println("  NM: -- none --");
        } else {
            for (NameModel dnm : repModel.getDisplayNames()) {
                System.out.println("  NM: " + dnm.getLocale() + " --> " + dnm.getName());
            }
        }

        if (repModel.getChildren() != null) {
            for (PlaceRepresentationModel childModel : repModel.getChildren()) {
                printIt(">>Child", childModel);
            }
        }
    }
}
