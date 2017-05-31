package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class ExtXrefTypeCreate {

    /** Base URL of the application */
    private static String xrefationUrl = "http://localhost:8080/std-ws-place/places/xref-types";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        LocalizedNameDescModel nameDesc01 = new LocalizedNameDescModel();
        nameDesc01.setLocale("en");
        nameDesc01.setName("NGA USA");
        nameDesc01.setDescription("NGA United States UFI");

        List<LocalizedNameDescModel> names = new ArrayList<LocalizedNameDescModel>();
        names.add(nameDesc01);

        TypeModel typeModel = new TypeModel();
        typeModel.setId(0);
        typeModel.setCode("NGA_US_UFI");
        typeModel.setIsPublished(true);
        typeModel.setName(names);
        
        RootModel prModel = new RootModel();
        prModel.setType(typeModel);
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println(prModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        URL url = new URL(xrefationUrl);
        RootModel model = HttpHelper.doPOST(url, prModel);
        System.out.println("POST -- RM: " + model);
        TypeModel xrefModel = model.getType();
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println(model.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        if (xrefModel != null) {
            url = new URL(xrefationUrl + "/" + xrefModel.getId());
            model = HttpHelper.doGET(url);
            System.out.println("GET-ONE -- RM: " + model);
        }
    }
}
