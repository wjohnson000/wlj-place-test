package std.wlj.group;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.familysearch.standards.place.data.GroupDTO;
import org.familysearch.standards.place.data.GroupType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrDataService;

import std.wlj.util.SolrManager;


/**
 * Perform some read-only tests against place-reps ...
 * 
 * @author wjohnson000
 *
 */
public class CreateGroupSolr {

    public static void main(String... args) throws PlaceDataException {
        SolrDataService sdService = SolrManager.getLocalHttp();

        Set<GroupDTO> children = new HashSet<>();
        children.add(new GroupDTO(14, GroupType.PLACE_REP, new HashMap<String,String>(), new HashMap<String,String>(), true, new HashSet<Integer>()));
        children.add(new GroupDTO(16, GroupType.PLACE_REP, new HashMap<String,String>(), new HashMap<String,String>(), true, new HashSet<Integer>()));

        Map<String,String> names = new HashMap<>();
        names.put("en", "DD-en");
        names.put("ja", "DD-ja");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "DD-en");
        descr.put("ja", "DD-ja");
        GroupDTO xGroup = new GroupDTO(17, GroupType.PLACE_REP, names, descr, true, new HashSet<Integer>(), children);

        GroupDTO newGroup = sdService.create(xGroup, "wjohnson000");
        printIt(newGroup);

        System.exit(0);
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
