package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.PlaceTypeGroupModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class TestTypeGroups {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";

    private static Map<TypeModel,List<Integer>> typeToGroup = new TreeMap<TypeModel,List<Integer>>(new Comparator<TypeModel>() {
        @Override public int compare(TypeModel type01, TypeModel type02) {
            return type01.getId() - type02.getId();
        }
    });


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        readTypeGroups();
    }

    private static void readTypeGroups() throws Exception {
        URL url = new URL(baseUrl + "/type-groups/");
        RootModel model = HttpHelper.doGET(url);
        System.out.println("MODEL: " + model);

        int id = 1;
        while (true) {
            url = new URL(baseUrl + "/type-groups/" + id);
            model = HttpHelper.doGET(url);
            if (model == null) break;

            PlaceTypeGroupModel typeGroup = model.getPlaceTypeGroup();
            for (TypeModel type : typeGroup.getTypes()) {
                List<Integer> groups = typeToGroup.get(type);
                if (groups == null) {
                    groups = new ArrayList<Integer>();
                    typeToGroup.put(type, groups);
                }
                groups.add(id);
            }
            id++;
        }

        for (Map.Entry<TypeModel,List<Integer>> entry : typeToGroup.entrySet()) {
            System.out.print("Type=" + entry.getKey().getId() + "." + entry.getKey().getCode() + " --> " + entry.getValue().size() + "  [");
            for (Integer anId : entry.getValue()) {
                System.out.print(anId + ", ");
            }
            System.out.println("]");
        }

        System.exit(0);
    }
}
