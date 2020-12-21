/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.access;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;
import org.familysearch.standards.place.service.DbReadableService;

import std.wlj.util.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class TestConfirmIfParent {

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbReadableService readSvc = new DbReadableService(ds);

        List<Integer> parents = readSvc.confirmIfParent(Arrays.asList(1, 11, 111, 1_111, 11_111, 111_111));
        parents.forEach(System.out::println);

        readSvc.shutdown();
    }
}
