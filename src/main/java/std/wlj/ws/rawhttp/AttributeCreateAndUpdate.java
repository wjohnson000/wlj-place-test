package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.AttributeModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class AttributeCreateAndUpdate {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String awsUrl = "http://place-ws-aws.dev.fsglobal.org/std-ws-place/places";

    private static String appUrl = baseUrl;


    /**
     * Get attributes, create an attribute, get attributes, update an attribute,
     * get the attributes again.
     */
    public static void main(String[] args) throws Exception {
        int repId = 1;

        readAttributes(repId);
        AttributeModel attrModel = addAttribute(repId);
        readAttributes(repId);
        updateAttribute(repId, attrModel);
        readAttributes(repId);
    }

    private static void readAttributes(int repId) throws Exception {
        URL url = new URL(appUrl + "/reps/" + repId + "/attributes/");
        RootModel model = TestUtil.doGET(url);
        System.out.println("READ: " + model);
    }

    private static AttributeModel addAttribute(int repId) throws Exception {
        URL url = new URL(appUrl + "/reps/" + repId + "/attributes/");

        TypeModel attrType = new TypeModel();
        attrType.setId(433);
        attrType.setCode("FS_REG");

        AttributeModel attrModel = new AttributeModel();
        attrModel.setRepId(repId);
        attrModel.setType(attrType);
        attrModel.setValue("WLJ - TEST - NEWEST");
        attrModel.setYear(1999);
        attrModel.setLocale("en");

        RootModel model = new RootModel();
        model.setAttribute(attrModel);

        RootModel modelX = TestUtil.doPOST(url, model);
        System.out.println("CREATE: " + modelX);
        return modelX.getAttribute();
    }

    private static void updateAttribute(int repId, AttributeModel attrModel) throws Exception {
        URL url = new URL(appUrl + "/reps/" + repId + "/attributes/" + attrModel.getId());

        attrModel.setValue("WLJ - TEST - UPDATED");

        RootModel model = new RootModel();
        model.setAttribute(attrModel);

        RootModel modelX = TestUtil.doPUT(url, model);
        System.out.println("UPDATE: " + modelX);
    }
}
