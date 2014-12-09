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
        int repId = 79;

        int[] citnTypeIds = { 613 };
        for (int citnTypeId : citnTypeIds) {
            System.out.println("=========================================================================================");
            readCitations(repId);
            addCitation(repId, citnTypeId);
            readCitations(repId);
            System.out.println("=========================================================================================");
        }
    }

    private static void readCitations(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations?noCache=true");
        RootModel model = TestUtil.doGET(url);
        printIt("Read the model ...", repId, model);
    }

    private static CitationModel addCitation(int repId, int citnTypeId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations?noCache=true");

        TypeModel citnType = new TypeModel();
        citnType.setId(citnTypeId);
        citnType.setCode("ATTR");

        CitationModel citnModel = new CitationModel();
        citnModel.setRepId(repId);
        citnModel.setType(citnType);
        citnModel.setSourceId(1);
        citnModel.setSourceRef("test - abc - " + citnTypeId);
        citnModel.setDescription("This is a description." + citnTypeId);
        citnModel.setCitDate(new Date());

        RootModel model = new RootModel();
        model.setCitation(citnModel);
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println(model.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        RootModel modelX = TestUtil.doPOST(url, model);
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println(modelX.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        printIt("Add a new citation ...", repId, modelX);
        return (modelX == null) ? null : modelX.getCitation();
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
            System.out.println("  " + citnM.getId() +
                    "; type=" + citnM.getType().getCode() +
                    "; srcId=" + citnM.getSourceId() +
                    "; ref=" + citnM.getSourceRef() +
                    "; desc=" + citnM.getDescription() +
                    "; date=" + citnM.getCitDate());
        }
    }
}
