package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.Date;

import org.familysearch.standards.place.ws.model.CitationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class CitationCreateAndUpdate {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";


    /**
     * Get citations, create an citation, get citations, update an citation,
     * get the citations again.
     */
    public static void main(String[] args) throws Exception {
        int repId = 1111;

        readCitations(repId);
        CitationModel citnModel = addCitation(repId);
        if (citnModel != null) {
            readCitations(repId);
            updateCitation(repId, citnModel);
            readCitations(repId);
        }
    }

    private static void readCitations(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations/");
        RootModel model = TestUtil.doGET(url);
        System.out.println("READ: " + model);
    }

    private static CitationModel addCitation(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations/");

        TypeModel citnType = new TypeModel();
        citnType.setId(465);
        citnType.setCode("ATTR");

        CitationModel citnModel = new CitationModel();
        citnModel.setRepId(repId);
        citnModel.setType(citnType);
        citnModel.setSourceId(1);
        citnModel.setSourceRef("wlj - abc");
        citnModel.setDescription("This is a description");
        citnModel.setCitDate(new Date());

        RootModel model = new RootModel();
        model.setCitation(citnModel);

        RootModel modelX = TestUtil.doPOST(url, model);
        System.out.println("CREATE: " + modelX);
        return (modelX == null) ? null : modelX.getCitation();
    }

    @SuppressWarnings("deprecation")
    private static void updateCitation(int repId, CitationModel citnModel) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations/" + citnModel.getId());

        citnModel.setDescription("This is a description -- xxx");
        citnModel.setCitDate(new Date(114, 8, 25));

        RootModel model = new RootModel();
        model.setCitation(citnModel);

        RootModel modelX = TestUtil.doPUT(url, model);
        System.out.println("UPDATE: " + modelX);
    }

}
