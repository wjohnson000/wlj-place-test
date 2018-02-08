package std.wlj.dbload;

import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.loader.helper.SolrHelper;
import org.familysearch.standards.loader.impl.DeltaLoader;

import std.wlj.datasource.DbConnectionManager;

public class TestDeltaLoader {

    public static void main(String...args) {
        SolrHelper solrHelper = new SolrHelper("http://www.familysearch.org/int-solr/places");

        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper   dbHelper = new DbHelper(ds);

        DeltaLoader loader = new DeltaLoader("me-me-me");
        loader.init(dbHelper, solrHelper);

        List<Long> reps01 = loader.getRepsFromTransactionAndPlace(10057700L);
        System.out.println("Reps01: " + reps01.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(", ")));

        List<Long> reps02 = loader.getRepsFromTransactionAndRep(10057700L);
        System.out.println("Reps02: " + reps02.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(", ")));

        List<Long> reps03 = loader.getNewReps(10740900L);
        System.out.println("Reps03: " + reps03.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(", ")));
    }
}
