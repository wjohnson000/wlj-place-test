package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.SourceModel;


public class TestCreateSource {

    /** Base URL of the application */
    private static String sourceUrl = "https://beta.familysearch.org/int-std-ws-place/places/sources";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        HttpHelper.acceptType = "application/xml";
        HttpHelper.authId = "Bearer USYSD8D316A3B393EF2BB96F3198FF848C07_idses-refa06.a.fsglobal.net";

        SourceModel sourceModel = new SourceModel();
        sourceModel.setId(0);
        sourceModel.setTitle("wlj-title");
        sourceModel.setDescription("wlj-description");
        sourceModel.setPublished(true);
        
        RootModel prModel = new RootModel();
        prModel.setSource(sourceModel);

        URL url = new URL(sourceUrl);
        RootModel model = HttpHelper.doPOST(url, prModel);
        System.out.println("POST -- RM: " + model);
        SourceModel srcModel = model.getSource();

        // Read ALL sources
        url = new URL(sourceUrl);
        model = HttpHelper.doGET(url);
        System.out.println("GET-ALL -- Count: " + model.getSources().size());

        // Read only the one new source
        if (srcModel != null) {
            url = new URL(sourceUrl + "/" + srcModel.getId());
            model = HttpHelper.doGET(url);
            System.out.println("GET-ONE -- RM: " + model);
        }

        // Attempt to do an update ...
        srcModel.setTitle("wlj-title-new");
        srcModel.setDescription("wlj-description-new");

        prModel = new RootModel();
        prModel.setSource(srcModel);
        url = new URL(sourceUrl + "/" + srcModel.getId());
        model = HttpHelper.doPUT(url, prModel);
        System.out.println("PUT -- RM: " + model);

        // Read only the one new source
        if (srcModel != null) {
            url = new URL(sourceUrl + "/" + srcModel.getId());
            model = HttpHelper.doGET(url);
            System.out.println("GET-ONE -- RM: " + model);
        }
    }
}
