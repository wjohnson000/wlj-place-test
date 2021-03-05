/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.cleanup;

import java.util.List;

/**
 * List all collections in S3.  This is done by listing all FILES on S3 (HHS bucket) and looking for
 * files in the ".../collection" sub-folder.
 * 
 * @author wjohnson000
 *
 */
public class RunS3CollectionList {

    public static void main(String...args) {
        S3CollectionServices thisCS = new S3CollectionServices();
        List<String> collectionIds = thisCS.getCollectionIds();

        System.out.println("Collections:\n");
        collectionIds.forEach(System.out::println);

        System.exit(0);
    }
}
