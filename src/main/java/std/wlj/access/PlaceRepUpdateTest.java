package std.wlj.access;

import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;

public class PlaceRepUpdateTest {

    private static PlaceDataServiceImpl dataService;

    public static void main(String... args) {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        SolrService solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.5.0");
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        List<PlaceRepBridge> repBridges = dataService.getRepVersions(337);
        repBridges.forEach(rep -> dumpRep(rep));

        PlaceRepBridge repBridge = repBridges.get(repBridges.size()-1);
        int typeId = repBridge.getPlaceType().getTypeId();
        Integer groupId = repBridge.getChildConstraintTypeGroup() == null ? null : repBridge.getChildConstraintTypeGroup().getGroupId();
        Integer parentId = repBridge.getJurisdictionIdentifiers().length < 2 ? 0 : repBridge.getJurisdictionIdentifiers()[1];

        try {
            PlaceRepBridge newRepBridge = dataService.updateRep(
                    repBridge.getRepId(),                 // repId,
                    repBridge.getPlaceId(),               // placeId,
                    parentId,                             // parentId,
                    repBridge.getJurisdictionFromYear(),  // fromYear,
                    repBridge.getJurisdictionToYear(),    // toYear,
                    typeId,                               // typeId,
                    repBridge.getDefaultLocale(),         // defaultDisplayLocale,
                    repBridge.getAllDisplayNames(),       // displayNames,
                    repBridge.getLatitude(),              // latitude,
                    repBridge.getLongitude(),             // longitude,
                    repBridge.isPublished(),              // pubFlag,
                    repBridge.isValidated(),              // valFlag,
                    groupId,                              // childConstraintGroupId,
                    25,                                   // prefBoundaryId,
                    "wjohnson000",                        // userId,
                    null);                                // revision
            dumpRep(newRepBridge);
        } catch (PlaceDataException ex) {
            System.out.println("EX: " + ex.getClass().getSimpleName() + " --> " + ex.getMessage());
        }

        solrService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }

    static void dumpRep(PlaceRepBridge prBridge) {
        System.out.println("=======================================================================");
        System.out.println("ID: " + prBridge.getRepId() + ";  Rev: " + prBridge.getRevision());
        System.out.println("  place: " + prBridge.getPlaceId());
        System.out.println("  place: " + Arrays.toString(prBridge.getJurisdictionIdentifiers()));
        System.out.println("   type: " + prBridge.getPlaceType());
        System.out.println("  range: " + prBridge.getJurisdictionFromYear() + " to " + prBridge.getJurisdictionToYear());
        System.out.println("  where: " + prBridge.getLatitude() + " :: " + prBridge.getLongitude());
        System.out.println("  bdyId: " + prBridge.getPreferredBoundaryId());
        System.out.println("  " + prBridge.getAllDisplayNames());
        System.out.println();
    }
}
