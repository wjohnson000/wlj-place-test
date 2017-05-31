package std.wlj.jira;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.CitationModel;
import org.familysearch.standards.place.ws.model.RootModel;

import std.wlj.ws.rawhttp.HttpHelper;


public class STD2663ProdServer {

    /** Base URL of the application */
    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places";


    /**
     * Get citations, create an citation, get citations, update an citation,
     * get the citations again.
     */
    public static void main(String[] args) throws Exception {
        int repId = 10315646;
        getRep(repId);
        readCitations(repId);
        removeCitation(repId, 64318273);
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "?noCache=true");
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }

    private static void readCitations(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations?noCache=true");
        RootModel model = HttpHelper.doGET(url);
        printIt("Read the model ...", repId, model);
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

    /**
     * Remove a citation from a place-rep
     * @param repId place-rep identifier
     * @param citnId citation identifier
     * @return
     * @throws Exception
     */
    private static void removeCitation(int repId, int citnId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations/" + citnId + "?noCache=true");

        HttpHelper.doDELETE(url);
    }

}
