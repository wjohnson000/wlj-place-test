package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.List;

import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.VariantModel;


/**
 * Do some or all of the following:
 * <br>-- read a place
 * <br>-- update a place
 * 
 * @author wjohnson000
 */
public class STD2639 {

    /** Base URL of the application */
    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places";


    public static void main(String[] args) throws Exception {
        PlaceModel placeModel;
        PlaceRepresentationModel repModel;

        placeModel = getPlace(3288484);
        printIt("Original PRG-Model", placeModel);
        repModel = getRep(10307352);
        printIt("REP-Model", repModel);
        repModel = getRep(10304586);
        printIt("REP-Model", repModel);

        System.out.println("\n========================================================================================\n");
        placeModel = getPlace(1954297);
        printIt("Original PRG-Model", placeModel);
        repModel = getRep(5948828);
        printIt("REP-Model", repModel);
        repModel = getRep(428892);
        printIt("REP-Model", repModel);
    }

    private static PlaceModel getPlace(int plcId) throws Exception {
        URL url = new URL(baseUrl + "/" + plcId);
        RootModel responseModel = TestUtil.doGET(url);
        return responseModel.getPlace();
    }

    private static PlaceRepresentationModel getRep(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "?children=true");
        RootModel model = TestUtil.doGET(url);
        return model.getPlaceRepresentation();
    }

    private static void printIt(String header, PlaceModel plcModel) {
        System.out.println("\n" + header);
        System.out.println("  ID: " + plcModel.getId());
        System.out.println("  FM: " + plcModel.getFromYear());
        System.out.println("  TO: " + plcModel.getToYear());
        for (VariantModel pnm : plcModel.getVariants()) {
            System.out.println("  NM: " + pnm.getId() + " --> " + pnm.getType().getCode() + " :: " + pnm.getName().getLocale() + "." + pnm.getName().getName());
        }
        List<PlaceRepresentationModel> reps = plcModel.getReps();
        if (reps != null) {
            for (PlaceRepresentationModel rep : reps) {
                System.out.println("  RP: " + rep.getId());
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

        for (NameModel dnm : repModel.getDisplayNames()) {
            System.out.println("  NM: " + dnm.getLocale() + " --> " + dnm.getName());
        }

        if (repModel.getChildren() != null) {
            for (PlaceRepresentationModel childModel : repModel.getChildren()) {
                printIt(">>Child", childModel);
            }
        }
    }
}
