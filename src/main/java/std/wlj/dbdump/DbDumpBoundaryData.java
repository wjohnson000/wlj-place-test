package std.wlj.dbdump;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.DbConnectionManager;

public class DbDumpBoundaryData {

    static final String fileBase = "C:/temp/db-dump/boundary-data";

    static final String query =
        "SELECT rep_id, " +
        "       boundary_id, " +
        "       tran_id, " +
        "       ST_AsKML(boundary_data, 7) AS kml " +
        "  FROM rep_boundary " +
        " ORDER BY rep_id, boundary_id, tran_id";

    public static void main(String... args) throws Exception {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        List<String> bdyDetails = dbHelper.getGenericRows(query, "|", "rep_id", "boundary_id", "kml");
        long time1 = System.nanoTime();

        for (String bdyDetail : bdyDetails) {
            String[] chunks = PlaceHelper.split(bdyDetail, '|');
            if (chunks.length == 3) {
                String fileName = chunks[0] + "-" + chunks[1] + ".txt";
                Files.write(Paths.get(fileBase, fileName), Arrays.asList(chunks[2]), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            }
        }

        System.out.println("BOUNDARY.Row-count: " + bdyDetails.size() + " .. Time=" + (time1-time0) / 1_000_000.0);
        if (args.length == 0) {
            System.exit(0);
        }
    }
}
