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

    static final int    PAGE_SIZE = 1000;
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

        int count = 0;
        int iters = 0;
        int offset = 0;
        boolean again = true;
        long time0 = System.nanoTime();

        while (again) {
            iters++;

            String pagedQuery = query + " LIMIT " + PAGE_SIZE + " OFFSET " + offset;
            List<String> bdyDetails = dbHelper.getGenericRows(pagedQuery, "|", "rep_id", "boundary_id", "kml");
            again = bdyDetails.size() < PAGE_SIZE;
            offset += PAGE_SIZE;
            count  += bdyDetails.size();
            System.out.println("   BOUNDARY.iter=" + iters + " .. Count=" + count);
            
            for (String bdyDetail : bdyDetails) {
                String[] chunks = PlaceHelper.split(bdyDetail, '|');
                if (chunks.length == 3) {
                    String fileName = chunks[0] + "-" + chunks[1] + ".txt";
                    Files.write(Paths.get(fileBase, fileName), Arrays.asList(chunks[2]), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                }
            }
        }

        long time1 = System.nanoTime();
        System.out.println("BOUNDARY.Row-count: " + count + " .. Iters=" + iters + " .. Time=" + (time1-time0) / 1_000_000.0);

        if (args.length == 0) {
            System.exit(0);
        }
    }
}
