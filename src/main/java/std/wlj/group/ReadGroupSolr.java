package std.wlj.group;

import java.util.Set;

import org.familysearch.standards.place.data.GroupDTO;
import org.familysearch.standards.place.data.GroupType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrDataService;

import std.wlj.util.SolrManager;


public class ReadGroupSolr {
    public static void main(String... args) throws PlaceDataException {
        SolrDataService sdService = SolrManager.getLocalHttp();

        Set<GroupDTO> prGroups = sdService.getAllGroups(GroupType.PLACE_REP);
        System.out.println("Place-Rep groups ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }

        prGroups = sdService.getAllGroups(GroupType.PLACE_TYPE);
        System.out.println("\nPlace-Type groups ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }

        GroupDTO aGroup = sdService.getGroupById(GroupType.PLACE_TYPE, 10);
        System.out.println("\nGroup, ID=10");
        printIt(aGroup);

        try {
            aGroup = sdService.getGroupById(GroupType.PLACE_REP, 10);
            System.out.println("\nGroup, ID=10, wrong type");
            printIt(aGroup);
        } catch(PlaceDataException ex) {
            System.out.println("Expected exception ...");
        }

        prGroups = sdService.getGroupsByMemberId(GroupType.PLACE_TYPE, 250);
        System.out.println("\nPlace-Type groups having '250' as member ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }

        prGroups = sdService.getGroupsByMemberId(GroupType.PLACE_REP, 250);
        System.out.println("\nPlace-Type groups having '250' as member, wrong type ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }
    }

    private static void printIt(GroupDTO groupDTO) {
        System.out.println("  " + groupDTO.getId() + " --> " + groupDTO.getGroupType());
        System.out.println("    Member.count=" + groupDTO.getMemberIds().size());
        System.out.println("    Child.group.count=" + groupDTO.getChildGroups().size());
        for (String locale : groupDTO.getNames().keySet()) {
            System.out.println("    " + locale + " --> name=" + groupDTO.getName(locale) + ", desc=" + groupDTO.getDescription(locale));
        }
    }
}
