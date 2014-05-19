package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class TestCreateCitation {

    /** Base URL of the application */
    private static String citationUrl = "http://localhost:8080/std-ws-place/places/citation-types";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        LocalizedNameDescModel nameDesc01 = new LocalizedNameDescModel();
        nameDesc01.setLocale("en");
        nameDesc01.setName("wlj-name-en");
        nameDesc01.setDescription("wlj-desc-en");

        LocalizedNameDescModel nameDesc02 = new LocalizedNameDescModel();
        nameDesc02.setLocale("fr");
        nameDesc02.setName("wlj-name-fr");
        nameDesc02.setDescription("wlj-desc-fr");

        LocalizedNameDescModel nameDesc03 = new LocalizedNameDescModel();
        nameDesc03.setLocale("de");
        nameDesc03.setName("wlj-name-de");
        nameDesc03.setDescription("wlj-desc-de");

        List<LocalizedNameDescModel> names = new ArrayList<LocalizedNameDescModel>();
        names.add(nameDesc01);
        names.add(nameDesc02);
        names.add(nameDesc03);

        TypeModel typeModel = new TypeModel();
        typeModel.setId(0);
        typeModel.setCode("wlj-citation-y4");
        typeModel.setIsPublished(true);
        typeModel.setName(names);
        
        RootModel prModel = new RootModel();
        prModel.setType(typeModel);

        URL url = new URL(citationUrl);
        RootModel model = TestUtil.doPOST(url, prModel);
        System.out.println("POST -- RM: " + model);
        TypeModel citModel = model.getType();

        url = new URL(citationUrl);
        model = TestUtil.doGET(url);
        System.out.println("GET-ALL -- RM: " + model);

        if (citModel != null) {
            url = new URL(citationUrl + "/" + citModel.getId());
            model = TestUtil.doGET(url);
            System.out.println("GET-ONE -- RM: " + model);
        }
    }
}
