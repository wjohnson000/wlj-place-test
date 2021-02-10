/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.cleanup;

import java.util.List;

/**
 * @author wjohnson000
 *
 */
public class RunListCollections {

    public static void main(String...args) {
        CleanupService thisCS = new CleanupService();
        List<String> collectionIds = thisCS.getCollectionIds();

        System.out.println("Collections:\n");
        collectionIds.forEach(System.out::println);

        System.exit(0);
    }
}
