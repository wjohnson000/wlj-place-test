package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.PlaceTypeGroupModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


/**
 * Do some or all of the following:
 * <br>-- read a place-type group
 * <br>-- add a member to a place-type group
 * <br>-- add a new subgroup to a place-type group
 * <br>-- remove a subgroup from a place-type group
 * 
 * @author wjohnson000
 */
public class TypeGroupUPDATE {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/type-groups";
    private static String baseUrl = "http://54.204.45.169:8080/std-ws-place/places/type-groups";

    public static void main(String[] args) throws Exception {
        PlaceTypeGroupModel ptgModel = getPRGroup(37);
        printIt("Original PRG-Model", ptgModel);

//        PlaceTypeGroupModel updPrgModel = addSubGroup(ptgModel, "en", "name-y", "desc-y");
//        printIt("Updated PRG-Model", updPrgModel);

        PlaceTypeGroupModel updPrgModel = addPlaceTypes(ptgModel, 8, 9, 10);
        printIt("Updated PRG-Model", updPrgModel);

//        ptgModel = getPRGroup(37);
//        printIt("Updated-x PRG-Model", ptgModel);
    }

    private static PlaceTypeGroupModel getPRGroup(int prgId) throws Exception {
        URL url = new URL(baseUrl + "/" + prgId);
        RootModel responseModel = HttpHelper.doGET(url);
        return responseModel.getPlaceTypeGroup();
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

    private static PlaceTypeGroupModel addPlaceTypes(PlaceTypeGroupModel existing, int... repIds) throws Exception {
        URL url = new URL(baseUrl + "/" + existing.getId());

        List<TypeModel> theTypes = new ArrayList<>();
        for (int repId : repIds) {
            TypeModel aType = new TypeModel();
            aType.setId(repId);
            theTypes.add(aType);
        }
        existing.setTypes(theTypes);

        RootModel inModel = new RootModel();
        inModel.setPlaceTypeGroup(existing);
System.out.println("In Model:\n" + inModel.toJSON());
        RootModel responseModel = HttpHelper.doPUT(url, inModel);
        return responseModel.getPlaceTypeGroup();
    }

    private static PlaceTypeGroupModel addSubGroup(PlaceTypeGroupModel existing, String... termData) throws Exception {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        for (int i=0;  i<termData.length;  i+=3) {
            theNameDesc.add(makeNameAndDesc(termData[i], termData[i+1], termData[i+2]));
        }

        PlaceTypeGroupModel model = new PlaceTypeGroupModel();
        model.setId(0);
        model.setPublished(true);
        model.setName(theNameDesc);

        URL url = new URL(baseUrl);
        RootModel requestModel = new RootModel();
        requestModel.setPlaceTypeGroup(model);
        RootModel responseModel = HttpHelper.doPOST(url, requestModel);

        existing.getSubGroups().add(responseModel.getPlaceTypeGroup());

        url = new URL(baseUrl + "/" + existing.getId());
        requestModel = new RootModel();
        requestModel.setPlaceTypeGroup(existing);
        responseModel = HttpHelper.doPUT(url, requestModel);
        return responseModel.getPlaceTypeGroup();
    }

    private static void printIt(String header, PlaceTypeGroupModel ptgModel) {
        System.out.println("\n" + header);
        System.out.println("  ID: " + ptgModel.getId());
        for (LocalizedNameDescModel ndm : ptgModel.getLocalizedName()) {
            System.out.println("  Name: " + ndm.getLocale() + " --> " + ndm.getName() + " . " + ndm.getDescription());
        }
        for (TypeModel typeModel : ptgModel.getTypes()) {
            System.out.println("  Rep: " + typeModel.getId() + " --> " + typeModel.getCode() + " . " + typeModel.getLocalizedName().get(0).getName());
        }
        for (PlaceTypeGroupModel subGroup : ptgModel.getSubGroups()) {
            System.out.println("  SubGrp: " + subGroup.getId() + " --> " + subGroup.getLocalizedName().get(0).getName());
        }
    }
}
