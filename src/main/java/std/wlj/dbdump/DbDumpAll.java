/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

/**
 * @author wjohnson000
 *
 */
public class DbDumpAll {

    public static void main(String... args) throws Exception {
        DbDumpAltJurisdictions.main("skip-exit");
        DbDumpAttributes.main("skip-exit");
        DbDumpCitations.main("skip-exit");
        DbDumpDisplayNames.main("skip-exit");
        DbDumpExtXrefs.main("skip-exit");
        DbDumpPlaceReps.main("skip-exit");
        DbDumpPlaces.main("skip-exit");
        DbDumpTransactions.main("skip-exit");
        DbDumpVariantNames.main("skip-exit");

        DbDumpRepIdChain.main("skip-exit");
        DbDumpBoundaries.main("skip-exit");
        DbDumpBoundaryData.main("skip-exit");
    }
}
