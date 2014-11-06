package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;
import org.familysearch.standards.place.ws.model.VariantModel;


/**
 * Do some or all of the following:
 * <br>-- read a place
 * <br>-- update a place
 * 
 * @author wjohnson000
 */
public class PlaceUPDATE {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places";
//    private static String baseUrl = "http://www.familysearch.org/int-std-ws-place/places";
    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places";


    public static void main(String[] args) throws Exception {
        PlaceModel placeModel = getPlace(1337578);
        printIt("Original PRG-Model", placeModel);

        placeModel = getPlace(962649);
        printIt("Original PRG-Model", placeModel);

//        PlaceModel updPlaceModel = updateFromTo(placeModel, 1800, 1995);
//        PlaceModel updPlaceModel = addVariant(placeModel, 437, "en", "Provo Rocks");
//        printIt("Updated PRG-Model", updPlaceModel);
    }

    private static PlaceModel getPlace(int plcId) throws Exception {
        URL url = new URL(baseUrl + "/" + plcId);
        RootModel responseModel = TestUtil.doGET(url);
        return responseModel.getPlace();
    }

    private static PlaceModel updateFromTo(PlaceModel existing, Integer fromYr, Integer toYr) throws Exception {
        URL url = new URL(baseUrl + "/" + existing.getId());

        existing.setFromYear(fromYr);
        existing.setToYear(toYr);

        RootModel inModel = new RootModel();
        inModel.setPlace(existing);

        RootModel responseModel = TestUtil.doPUT(url, inModel);
        System.out.println("RM: " + responseModel);

        return responseModel.getPlace();
    }

    private static PlaceModel addVariant(PlaceModel existing, int typeId, String locale, String name) throws Exception {
        URL url = new URL(baseUrl + "/" + existing.getId());

        TypeModel typeModel = new TypeModel();
        typeModel.setId(typeId);

        NameModel nameModel = new NameModel();
        nameModel.setLocale(locale);
        nameModel.setName(name);

        VariantModel varModel = new VariantModel();
        varModel.setType(typeModel);
        varModel.setName(nameModel);
        existing.getVariants().add(varModel);

        RootModel inModel = new RootModel();
        inModel.setPlace(existing);

        RootModel responseModel = TestUtil.doPUT(url, inModel);
        System.out.println("RM: " + responseModel);

        return responseModel.getPlace();
    }

    private static void printIt(String header, PlaceModel plcModel) {
        System.out.println("\n" + header);
        System.out.println("  ID: " + plcModel.getId());
        System.out.println("  FM: " + plcModel.getFromYear());
        System.out.println("  TO: " + plcModel.getToYear());
        for (VariantModel pnm : plcModel.getVariants()) {
            System.out.println("  NM: " + pnm.getId() + " --> " + pnm.getType().getCode() + " :: " + pnm.getName().getLocale() + "." + pnm.getName().getName());
        }
    }
}
