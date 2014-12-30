package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class AllTypeGetAll {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places/";
//    private static String baseUrl = "http://ec2-54-235-166-8.compute-1.amazonaws.com:8080/std-ws-place/places/";

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
    	String[] typeSvcs = {
    		"attribute-types",
    		"citation-types",
    		"xref-types",
    		"name-types",
    		"place-types",
    		"resolution-types"
    	};

    	Map<Integer,String> typeData = new TreeMap<>();

    	for (String typeSvc : typeSvcs) {
            URL url = new URL(baseUrl + typeSvc);
            RootModel model = TestUtil.doGET(url);
            for (TypeModel typeModel : model.getTypes()) {
            	typeData.put(typeModel.getId(), typeSvc + "." + typeModel.getCode());
            }
    	}

    	for (Map.Entry<Integer,String> entry : typeData.entrySet()) {
    		System.out.println(entry.getKey() + " --> " + entry.getValue());
    	}
    }
}
