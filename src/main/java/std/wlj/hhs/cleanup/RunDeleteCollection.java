/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.cleanup;

/**
 * @author wjohnson000
 *
 */
public class RunDeleteCollection {

    public static void main(String...args) {
        String collectionId = "AAAA-wlj";
        CleanupService thisCS = new CleanupService();

        boolean delOK = thisCS.deleteCollection(collectionId);
        System.out.println("DELETE? " + delOK);

        System.exit(0);
    }
}
