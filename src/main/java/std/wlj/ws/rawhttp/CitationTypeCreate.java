package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class CitationTypeCreate {

    /** Base URL of the application */
    private static String citationUrl = "http://localhost:8080/std-ws-place/places/citation-types";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        LocalizedNameDescModel nameDesc01 = new LocalizedNameDescModel();
        nameDesc01.setLocale("en");
        nameDesc01.setName("cit-type-new-en");
        nameDesc01.setDescription("cit-type-new-desc-en");

        LocalizedNameDescModel nameDesc02 = new LocalizedNameDescModel();
        nameDesc02.setLocale("fr");
        nameDesc02.setName("cit-type-new-fr");
        nameDesc02.setDescription("cit-type-new-desc-fr");

        LocalizedNameDescModel nameDesc03 = new LocalizedNameDescModel();
        nameDesc03.setLocale("de");
        nameDesc03.setName("cit-type-new-de");
        nameDesc03.setDescription("cit-type-new-desc-de");

        List<LocalizedNameDescModel> names = new ArrayList<LocalizedNameDescModel>();
        names.add(nameDesc01);
        names.add(nameDesc02);
        names.add(nameDesc03);

        TypeModel typeModel = new TypeModel();
        typeModel.setId(0);
        typeModel.setCode("CIT-NEW-DUMMY");
        typeModel.setPublished(true);
        typeModel.setName(names);
        
        RootModel prModel = new RootModel();
        prModel.setType(typeModel);
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println(prModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        URL url = new URL(citationUrl);
        RootModel model = HttpHelper.doPOST(url, prModel);
        System.out.println("POST -- RM: " + model);
        TypeModel citModel = model.getType();
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println(model.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        if (citModel != null) {
            url = new URL(citationUrl + "/" + citModel.getId());
            model = HttpHelper.doGET(url);
            System.out.println("GET-ONE -- RM: " + model);
        }
    }
}
