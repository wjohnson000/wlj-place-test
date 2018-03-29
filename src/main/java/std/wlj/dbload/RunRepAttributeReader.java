package std.wlj.dbload;

import java.io.File;
import java.util.List;

import org.familysearch.standards.loader.model.RepAuxDataModel;
import org.familysearch.standards.loader.reader.RepAttributeReader;

public class RunRepAttributeReader {

    public static void main(String...args) {
        File baseDir = new File("C:/temp/flat-file/xxx");
        RepAttributeReader reader = new RepAttributeReader(baseDir);

        reader.openReader();
        List<RepAuxDataModel> attrs = reader.getReaderData(1);

        int repId = reader.nextRepId();
        while (repId > 0) {
            attrs = reader.getReaderData(repId);
            System.out.println("\n==================================================================");
            System.out.println("REP: " + repId);
            attrs.forEach(attr -> System.out.println("  " + attr.value));
            repId = reader.nextRepId();
        }

        reader.closeReader();
    }
}
