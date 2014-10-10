package std.wlj.group;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.sql.DataSource;

import org.familysearch.standards.place.data.GroupDTO;
import org.familysearch.standards.place.data.GroupType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.service.DbReadableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Perform some tests w/ place-type groups
 * 
 * @author wjohnson000
 *
 */
public class CreateGroup {

    private static DataSource ds;
    private static DbReadableService dataService;

    public static void main(String... args) throws PlaceDataException {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
        ds = (DataSource)appContext.getBean("dataSource");
        dataService = new DbReadableService(ds);

        Map<String,String> names = new HashMap<>();
        names.put("en", "DD-en");
        names.put("ja", "DD-ja");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "DD-en");
        descr.put("ja", "DD-ja");
        GroupDTO xGroup = new GroupDTO(0, GroupType.PLACE_TYPE, names, descr, true, new HashSet<Integer>(Arrays.asList(111, 222, 333)));

        GroupDTO newGroup = dataService.create(xGroup, "wjohnson000");
        System.out.println("\nNEW --------------------------------------------");
        printIt(newGroup);

        GroupDTO aGroup = dataService.getGroupById(GroupType.PLACE_TYPE, newGroup.getId());
        System.out.println("\nREAD -------------------------------------------");
        printIt(aGroup);

        xGroup = new GroupDTO(newGroup.getId(), GroupType.PLACE_TYPE, new HashMap<String,String>(), new HashMap<String,String>(), true, new HashSet<Integer>(Arrays.asList(111, 222)));
        GroupDTO updGroup = dataService.update(xGroup, "wjohnson000");
        System.out.println("\nUPDATE 1 ----------------------------------------");
        printIt(updGroup);

        xGroup = new GroupDTO(newGroup.getId(), GroupType.PLACE_TYPE, new HashMap<String,String>(), new HashMap<String,String>(), true, new HashSet<Integer>(Arrays.asList(111, 222, 333, 444)));
        updGroup = dataService.update(xGroup, "wjohnson000");
        System.out.println("\nUPDATE 2 ----------------------------------------");
        printIt(updGroup);

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
