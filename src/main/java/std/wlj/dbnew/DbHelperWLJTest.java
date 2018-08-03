package std.wlj.dbnew;

import java.util.Map;

import javax.sql.DataSource;

import std.wlj.datasource.DbConnectionManager;

public class DbHelperWLJTest {

    public static void main(String...args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelperWLJ dbHelp = new DbHelperWLJ(ds);

        System.out.println("Place stuff .......... ");
        Map<Integer, Integer> stuff = dbHelp.getPlaceAndRevn(11209900L);
        stuff.entrySet().forEach(System.out::println);

        System.out.println("\n\nRep stuff .......... ");
        stuff = dbHelp.getRepAndRevn(11209900L);
        stuff.entrySet().forEach(System.out::println);
    }
}
