package std.wlj.compression;

import org.familysearch.standards.place.ws.model.RootModel;

public class TestPOJOMarshaler {

    public static void main(String...args) throws Exception {
        String xml = Util.readInterpXML();
        String json = Util.readInterpJSON();

        System.out.println("LEN.xml=" + xml.length());
        System.out.println("LEN.jsn=" + json.length());
    }
}
