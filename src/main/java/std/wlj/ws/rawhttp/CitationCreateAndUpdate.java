package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.familysearch.standards.place.ws.model.CitationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class CitationCreateAndUpdate {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";


    /**
     * Get citations, create an citation, get citations, update an citation,
     * get the citations again.
     */
    public static void main(String[] args) throws Exception {
        int repId = 188111;

        readCitations(repId);
        CitationModel citnModel = addCitation(repId);
        if (citnModel != null) {
            readCitations(repId);
            updateCitation(repId, citnModel);
            readCitations(repId);
        }
    }

    private static void readCitations(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations?noCache=true");
        RootModel model = TestUtil.doGET(url);
        printIt("Read the model ...", repId, model);
    }

    private static CitationModel addCitation(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations?noCache=true");

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
        printIt("Add a new citation ...", repId, modelX);
        return (modelX == null) ? null : modelX.getCitation();
    }

    @SuppressWarnings("deprecation")
    private static void updateCitation(int repId, CitationModel citnModel) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations/" + citnModel.getId() + "?noCache=true");

        citnModel.setDescription("This is a description -- xxx");
        citnModel.setCitDate(new Date(114, 8, 25));

        RootModel model = new RootModel();
        model.setCitation(citnModel);

        RootModel modelX = TestUtil.doPUT(url, model);
        printIt("Update an existing citation ...", repId, modelX);
    }

    private static void printIt(String msg, int repId, RootModel rootModel) {
        System.out.println("---------------------------------------------------------------------");
        System.out.println(msg);
        System.out.println("RepId=" + repId);

        List<CitationModel> citnModels = new ArrayList<>();
        CitationModel citnModel = rootModel.getCitation();
        if (citnModel != null) {
            citnModels.add(citnModel);
        }

        if (rootModel.getCitations() != null) {
            citnModels.addAll(rootModel.getCitations());
        }

        for (CitationModel citnM : citnModels) {
            System.out.println("  " + citnM.getId() + "; date=" + citnM.getCitDate() + "; ref=" + citnM.getSourceRef());
        }
    }
}
