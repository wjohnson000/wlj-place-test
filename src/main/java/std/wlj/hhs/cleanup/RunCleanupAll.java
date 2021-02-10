/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.cleanup;

import java.util.*;

/**
 * @author wjohnson000
 *
 */
public class RunCleanupAll {

    static final String[] realCollections = {
        "MMMS-8LV",    // Quizzes
        "MMM3-YPZ",    // Neat-o test
        "MMM3-XWP",    // Neat-o test
        "MMM9-X78",    // Oxford Given
        "MMMS-MRM",    // Load Test
        "MMM3-G4V",    // AAM Trending
        "MMM3-YP8",    // Neat-o test
        "MMMS-X62",    // AAM Events
        "MMM9-FRZ",    // Oxford Surnames
        "MMM9-5L2",    // Temp, for API development
        "MMM3-FJ4",    // Oxford GIven
        "MMMS-TBP",    // AAM Events
        "MMM3-T29",    // Neat-o test
        "MMM3-N3Y",    // Oxford Given (testing)
        "MMMS-7HK",    // ROC Praenominia
        "MMM9-X7D",    // ROC First and Last
        "MMM9-DFC",    // French Geneanet
        "unassigned",  // Special collection ... do not remove!
    };

    static final CleanupService thisCS = new CleanupService();

    public static void main(String...arsg) {
        List<String> collIds = thisCS.getCollectionIds();

        for (String collId : collIds) {
            System.out.println("\n==============================================================");
            boolean isReal = Arrays.stream(realCollections).anyMatch(id -> id.equalsIgnoreCase(collId));

            System.out.println(collId + " >>> IS REAL ? " + isReal);
            if (isReal) {
//                RunListCollectionDetail.showCollectionDetails(thisCS, collId);
            } else {
                System.out.println("Deleting collection: " + collId);
                thisCS.deleteCollection(collId);
            }
        }
    }
}
