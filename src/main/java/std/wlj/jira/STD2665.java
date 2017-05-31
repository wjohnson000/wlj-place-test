package std.wlj.jira;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;
import org.familysearch.standards.place.ws.model.VariantModel;

import std.wlj.ws.rawhttp.HttpHelper;


/**
 * Can't add two variant names w/ the same type and locale
 * 
 * @author wjohnson000
 */
public class STD2665 {

    /** Base URL of the application */
    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places";


    public static void main(String[] args) throws Exception {
    	int placeId = 3288486;
        PlaceModel placeModel = getPlace(placeId);
        printIt("Original PLACE-Model", placeModel);

        PlaceModel updateModel = updatePlace(placeModel);
        printIt("New PLACE-Model", updateModel);

        PlaceModel placeModelX = getPlace(placeId);
        printIt("New[2] PLACE-Model", placeModelX);
    }

    /**
     * Return a PLACE
     * 
     * @param plcId place identifier
     * @return
     * @throws Exception
     */
    private static PlaceModel getPlace(int plcId) throws Exception {
        URL url = new URL(baseUrl + "/" + plcId + "?noCache=true");
        RootModel responseModel = HttpHelper.doGET(url);
        return responseModel.getPlace();
    }

    /**
     * Update a PLACE by adding three variant names, all of the same type and locale
     * @param currModel current PLACE
     * 
     * @return update PLACE
     * @throws Exception
     */
    private static PlaceModel updatePlace(PlaceModel currModel) throws Exception {
    	URL url = new URL(baseUrl + "/" + currModel.getId() + "?noCache=true");

        List<VariantModel> varNames = new ArrayList<>();
        varNames.add(makeVariant("Ploskoya", "en", 440, "var"));
        varNames.add(makeVariant("Ploskoye", "en", 440, "var"));
        varNames.add(makeVariant("Ploskoyo", "en", 440, "var"));

        currModel.getVariants().addAll(varNames);
        RootModel inModel = new RootModel();
        inModel.setPlace(currModel);

        RootModel outModel = HttpHelper.doPUT(url, inModel);
        return (outModel == null) ? null : outModel.getPlace();
    }

    /**
     * Print out the detail of a PLACE
     * 
     * @param header some random text
     * @param plcModel PLACE
     */
    private static void printIt(String header, PlaceModel plcModel) {
        System.out.println("\n" + header);
        System.out.println("  PLC-ID: " + plcModel.getId());
        System.out.println("  FRM-YR: " + plcModel.getFromYear());
        System.out.println("  TO--YR: " + plcModel.getToYear());
        for (VariantModel pnm : plcModel.getVariants()) {
            System.out.println("  VAR_NM: " + pnm.getId() + " --> " + pnm.getType().getCode() + " :: " + pnm.getName().getLocale() + "." + pnm.getName().getName());
        }
        List<PlaceRepresentationModel> reps = plcModel.getReps();
        if (reps != null) {
            for (PlaceRepresentationModel rep : reps) {
                System.out.println("  REP-ID: " + rep.getId());
            }
        }
    }

    /**
     * Create a 'VariantModel' from the text, locale, name-type-id and name-type-code
     */
    private static VariantModel makeVariant(String text, String locale, int typeId, String typeCode) {
        NameModel name01 = new NameModel();
        name01.setLocale(locale);
        name01.setName(text);

        VariantModel vName01 = new VariantModel();
        vName01.setName(name01);
        vName01.setType(makeType(typeId, typeCode));
        return vName01;
    }

    /**
     * Create a 'TypeModel' from the identifier and code
     * 
     * @param id type identifier
     * @param code type code
     * @return TypeModle
     */
    private static TypeModel makeType(int id, String code) {
        TypeModel type = new TypeModel();
        type.setId(id);
        type.setCode(code);
        return type;
    }

}
