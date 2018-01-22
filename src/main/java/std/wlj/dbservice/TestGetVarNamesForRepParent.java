package std.wlj.dbservice;

import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.service.DbReadableService;

import std.wlj.datasource.DbConnectionManager;

public class TestGetVarNamesForRepParent {

    static DbReadableService dbService;

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceSams();
        dbService = new DbReadableService(ds);

//        dumpNameDetails(1);
//        dumpNameDetails(1);
//        dumpNameDetails(11);
//        dumpNameDetails(111);
//        dumpNameDetails(1111);
//        dumpNameDetails(11111);
//        dumpNameDetails(25945);
//        dumpNameDetails(25907);
//        dumpNameDetails(351953);
//        dumpNameDetails(351959);
//        dumpNameDetails(10579050);
//        dumpNameDetails(10625946);
        dumpNameDetails(392313);

        dbService.shutdown();
    }

    static void dumpNameDetails(int parentId) {
        System.out.println("\n\n=======================================================================");
        System.out.println("ParentId = " + parentId);

        long time00 = System.nanoTime();
        Set<PlaceNameBridge> names = dbService.getAllChildrenVariants(parentId);
        long time01 = System.nanoTime();

        System.out.println("\nInitial set of names ...");
        System.out.println("  Names.size=" + names.size() + " --> time=" + (time01-time00) / 1_000_000.0);

        time00 = System.nanoTime();
        Set<String> norm = names.stream().map(name -> name.getNormalizedName()).collect(Collectors.toSet());
        time01 = System.nanoTime();

        System.out.println("Normalized set of names ...");
        System.out.println("  Names.size=" + norm.size() + " --> time=" + (time01-time00) / 1_000_000.0);
        norm.stream()
            .filter(name -> name.startsWith("q"))
            .forEach(System.out::println);
    }
}
