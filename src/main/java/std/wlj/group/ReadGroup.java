package std.wlj.group;

import java.util.Set;

import javax.sql.DataSource;

import org.familysearch.standards.place.data.GroupDTO;
import org.familysearch.standards.place.data.GroupType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.service.DbReadableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Perform some read-only tests against place-reps ...
 * 
 * @author wjohnson000
 *
 */
public class ReadGroup {

    private static DataSource ds;
    private static DbReadableService dataService;

    public static void main(String... args) throws PlaceDataException {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
        ds = (DataSource)appContext.getBean("dataSource");
        dataService = new DbReadableService(ds);

        Set<GroupDTO> prGroups = dataService.getAllGroups(GroupType.PLACE_REP);
        System.out.println("Place-Rep groups ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }

        prGroups = dataService.getAllGroups(GroupType.PLACE_TYPE);
        System.out.println("\nPlace-Type groups ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }

        GroupDTO aGroup = dataService.getGroupById(GroupType.PLACE_TYPE, 10);
        System.out.println("\nGroup, ID=10");
        printIt(aGroup);

        try {
            aGroup = dataService.getGroupById(GroupType.PLACE_REP, 10);
            System.out.println("\nGroup, ID=10, wrong type");
            printIt(aGroup);
        } catch(PlaceDataException ex) {
            System.out.println("Expected exception ...");
        }

        prGroups = dataService.getGroupsByMemberId(GroupType.PLACE_TYPE, 250);
        System.out.println("\nPlace-Type groups having '250' as member ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }

        prGroups = dataService.getGroupsByMemberId(GroupType.PLACE_REP, 250);
        System.out.println("\nPlace-Type groups having '250' as member, wrong type ... count=" + prGroups.size());
        for (GroupDTO prGroup : prGroups) {
            printIt(prGroup);
        }

        ((ClassPathXmlApplicationContext)appContext).close();
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
