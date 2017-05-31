package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.SourceModel;


public class SourceStuff {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/sourceibute-types/";
    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places/sources/";


    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        readSources();
        SourceModel sourceModel = createSource();
        System.out.println("SrcModel: " + sourceModel);
    }

    /**
     * Read all sources; read a specific source; attempt to read a non-existent source
     * @throws Exception
     */
    private static void readSources() throws Exception {
        URL url;
        RootModel model;

        url = new URL(baseUrl);
        model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);

        url = new URL(baseUrl + "10");
        model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);

        url = new URL(baseUrl + "2020");
        model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }

    /**
     * Create a new source type ...
     * 
     * @throws Exception
     */
    private static SourceModel createSource() throws Exception {
        URL url = new URL(baseUrl);
        SourceModel theSource = new SourceModel();
        theSource.setTitle("WLJ Title ...");
        theSource.setDescription("WLJ description and nothing else  ...");
        theSource.setIsPublished(true);

        RootModel inModel = new RootModel();
        inModel.setSource(theSource);
        System.out.println("InModel: " + inModel);

        RootModel outModel = HttpHelper.doPOST(url, inModel);
        System.out.println("OutModel: " + outModel);
        return outModel.getSource();
    }
}
