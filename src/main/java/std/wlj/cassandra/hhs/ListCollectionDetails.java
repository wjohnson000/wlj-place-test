/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.List;

import std.wlj.cassandra.hhs.model.CollectionData;

/**
 * @author wjohnson000
 *
 */
public class ListCollectionDetails {

    public static void main(String... args) {
        CassandraHelper cHelper = new CassandraHelper();
        List<CollectionData> collData = cHelper.getCollections(true);
        for (CollectionData cData : collData) {
            System.out.println("\n====================================================================");
            System.out.println("  ID: " + cData.id);
            System.out.println("name: " + cData.name);
            System.out.println("desc: " + cData.description);
            System.out.println("lang: " + cData.languages);
            System.out.println("type: " + cData.types);
            System.out.println("mem#: " + cData.memberCount);
        }
    }
}
