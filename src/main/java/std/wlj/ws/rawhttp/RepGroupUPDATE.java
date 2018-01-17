package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.PlaceRepGroupModel;
import org.familysearch.standards.place.ws.model.PlaceRepSummaryModel;
import org.familysearch.standards.place.ws.model.RootModel;


/**
 * Do some or all of the following:
 * <br>-- read a place-rep group
 * <br>-- add a member to a place-rep group
 * <br>-- add a new subgroup to a place-rep group
 * <br>-- remove a subgroup from a place-rep group
 * 
 * @author wjohnson000
 */
public class RepGroupUPDATE {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/place-rep-groups";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    public static void main(String[] args) throws Exception {
        PlaceRepGroupModel prgModel = createPRGroup("en", "name-z", "desc-z");
        printIt("Original PRG-Model", prgModel);

        PlaceRepGroupModel newPrgModel = getPRGroup(prgModel.getId());
        printIt("Original PRG-Model", newPrgModel);

//        PlaceRepGroupModel updPrgModel = addSubGroup(prgModel, "en", "name-z", "desc-z");
//        printIt("Updated PRG-Model", updPrgModel);

        PlaceRepGroupModel updPrgModel = modifyPlaceReps(prgModel, 6, 7, 8);
        printIt("Updated PRG-Model", updPrgModel);

        updPrgModel = modifyPlaceReps(updPrgModel, 7, 8);
        printIt("Updated PRG-Model", updPrgModel);
    }

    private static PlaceRepGroupModel getPRGroup(int prgId) throws Exception {
        URL url = new URL(baseUrl + "/" + prgId);
        RootModel responseModel = HttpHelper.doGET(url);
        return responseModel.getPlaceRepGroup();
    }

    private static PlaceRepGroupModel createPRGroup(String... termData) throws Exception {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        for (int i=0;  i<termData.length;  i+=3) {
            theNameDesc.add(makeNameAndDesc(termData[i], termData[i+1], termData[i+2]));
        }

        PlaceRepGroupModel model = new PlaceRepGroupModel();
        model.setId(0);
        model.setPublished(true);
        model.setName(theNameDesc);
        model.setRepSummaries(new ArrayList<PlaceRepSummaryModel>());
        model.setSubGroups(new ArrayList<PlaceRepGroupModel>());

        URL url = new URL(baseUrl);
        RootModel requestModel = new RootModel();
        requestModel.setPlaceRepGroup(model);
        RootModel responseModel = HttpHelper.doPOST(url, requestModel);
        return responseModel.getPlaceRepGroup();

    }

    /**
     * Create a localized name given the locale, name and description
     * 
     * @param locale locale
     * @param name name
     * @param desc description
     * @return localized name/description
     */
    private static LocalizedNameDescModel makeNameAndDesc(String locale, String name, String desc) {
        LocalizedNameDescModel nameDesc = new LocalizedNameDescModel();

        nameDesc.setLocale(locale);
        nameDesc.setName(name);
        nameDesc.setDescription(desc);

        return nameDesc;
    }

    private static PlaceRepGroupModel modifyPlaceReps(PlaceRepGroupModel existing, int... repIds) throws Exception {
        URL url = new URL(baseUrl + "/" + existing.getId());

        List<PlaceRepSummaryModel> prSummaries = new ArrayList<>();
        for (int repId : repIds) {
            PlaceRepSummaryModel prsModel = new PlaceRepSummaryModel();
            prsModel.setId(repId);
            prSummaries.add(prsModel);
        }
        existing.setRepSummaries(prSummaries);

        RootModel inModel = new RootModel();
        inModel.setPlaceRepGroup(existing);

        RootModel responseModel = HttpHelper.doPUT(url, inModel);
        return responseModel.getPlaceRepGroup();
    }

    private static PlaceRepGroupModel addSubGroup(PlaceRepGroupModel existing, String... termData) throws Exception {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        for (int i=0;  i<termData.length;  i+=3) {
            theNameDesc.add(makeNameAndDesc(termData[i], termData[i+1], termData[i+2]));
        }

        PlaceRepGroupModel model = new PlaceRepGroupModel();
        model.setId(0);
        model.setPublished(true);
        model.setName(theNameDesc);

        URL url = new URL(baseUrl);
        RootModel requestModel = new RootModel();
        requestModel.setPlaceRepGroup(model);
        RootModel responseModel = HttpHelper.doPOST(url, requestModel);

        existing.getSubGroups().add(responseModel.getPlaceRepGroup());

        url = new URL(baseUrl + "/" + existing.getId());
        requestModel = new RootModel();
        requestModel.setPlaceRepGroup(existing);
        responseModel = HttpHelper.doPUT(url, requestModel);
        return responseModel.getPlaceRepGroup();
    }

    private static void printIt(String header, PlaceRepGroupModel prgModel) {
        System.out.println("\n" + header);
        System.out.println("  ID: " + prgModel.getId());
        for (LocalizedNameDescModel ndm : prgModel.getLocalizedName()) {
            System.out.println("  Name: " + ndm.getLocale() + " --> " + ndm.getName() + " . " + ndm.getDescription());
        }
        for (PlaceRepSummaryModel prsModel : prgModel.getRepSummaries()) {
            System.out.println("  Rep: " + prsModel.getId() + " --> " + prsModel.getType().getCode() + " . " + prsModel.getDisplayName().getName());
        }
        for (PlaceRepGroupModel subGroup : prgModel.getSubGroups()) {
            System.out.println("  SubGrp: " + subGroup.getId() + " --> " + subGroup.getLocalizedName().get(0).getName());
        }
    }
}
