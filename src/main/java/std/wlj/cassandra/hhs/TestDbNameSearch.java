/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import org.familysearch.homelands.core.persistence.CassandraOps;
import org.familysearch.homelands.core.persistence.dao.NameSearchDaoImpl;
import org.familysearch.homelands.core.persistence.model.NameSearchData;
import org.familysearch.homelands.lib.common.model.NameType;

/**
 * @author wjohnson000
 *
 */
public class TestDbNameSearch {
    public static void main(String... args) {
        try (CqlSession session = createSession()) {
            CassandraOps cassOps = new CassandraOps(session);
            NameSearchDaoImpl nsDao = new NameSearchDaoImpl(cassOps);
            addNameSearch(nsDao);
            searchExact(nsDao);
            searchVariants(nsDao);
            searchFuzzy(nsDao);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static CqlSession createSession() throws Exception {
        InetSocketAddress local = new InetSocketAddress("127.0.0.1", 9042);

        CqlSessionBuilder builder = CqlSession.builder()
                            .withKeyspace(CqlIdentifier.fromCql("hhs"))
                            .addContactPoints(Arrays.asList(local))
                            .withLocalDatacenter("dc1");

        return builder.build();
    }

    static void addNameSearch(NameSearchDaoImpl nsDao) throws Exception {
        NameSearchData nsd = new NameSearchData();
        nsd.setNameId("ABC-1235");
        nsd.setName("john");
        nsd.setType(NameType.FIRST);
        nsd.setVariants(Arrays.asList("jon", "jonny", "jonnie", "johan", "ivan", "juan"));
        nsDao.create(nsd);
        System.out.println("SUCCESS!!");
    }

    static void searchExact(NameSearchDaoImpl nsDao) throws Exception {
        List<String> ids = nsDao.searchExact("John", NameType.FIRST);
        ids.stream().forEach(id -> System.out.println("ex.ID: " + id));
    }

    static void searchVariants(NameSearchDaoImpl nsDao) throws Exception {
        List<String> ids = nsDao.searchVariant("IVAN", NameType.FIRST);
        ids.stream().forEach(id -> System.out.println("va.ID: " + id));
    }

    static void searchFuzzy(NameSearchDaoImpl nsDao) throws Exception {
        List<String> ids = nsDao.searchFuzzy("Jahn", NameType.FIRST);
        ids.stream().forEach(id -> System.out.println("fz.ID: " + id));
    }
}
