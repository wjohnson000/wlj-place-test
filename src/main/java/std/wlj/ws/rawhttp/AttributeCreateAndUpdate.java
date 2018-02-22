package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.AttributeModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class AttributeCreateAndUpdate {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";


    /**
     * Get attributes, create an attribute, get attributes, update an attribute,
     * get the attributes again.
     */
    public static void main(String[] args) throws Exception {
        int repId = 553594;

        readAttributes(repId);
        AttributeModel attrModel = addAttribute(repId);
        System.out.println("Attr: " + attrModel.getId() + " . " + attrModel.getFromYear() + " . " + attrModel.getToYear() + " . " + attrModel.getValue());
    }

    private static void readAttributes(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/attributes/");
        RootModel model = HttpHelper.doGET(url);
        for (AttributeModel aModel : model.getAttributes()) {
            System.out.println("ATT: " + aModel.getId() + "|" + aModel.getFromYear() + "|" + aModel.getToYear() + "|" + aModel.getLocale() + "|" + aModel.getValue());
        }
    }

    private static AttributeModel addAttribute(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/attributes/");

        TypeModel attrType = new TypeModel();
        attrType.setId(433);
        attrType.setCode("FS_REG");

        AttributeModel attrModel = new AttributeModel();
        attrModel.setRepId(repId);
        attrModel.setType(attrType);
        attrModel.setValue("WLJ - TEST - NEWEST");
        attrModel.setFromYear(1930);
        attrModel.setToYear(2030);
        attrModel.setLocale("en");

        RootModel model = new RootModel();
        model.setAttribute(attrModel);

        RootModel modelX = HttpHelper.doPOST(url, model);
        System.out.println("CREATE: " + modelX);
        return modelX.getAttribute();
    }
}
