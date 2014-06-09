package std.wlj.group;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.sql.DataSource;

import org.familysearch.standards.place.data.GroupDTO;
import org.familysearch.standards.place.data.GroupType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Perform some read-only tests against place-reps ...
 * 
 * @author wjohnson000
 *
 */
public class CreateGroup {

    private static DataSource ds;
    private static DbDataService dataService;

    public static void main(String... args) throws PlaceDataException {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
        ds = (DataSource)appContext.getBean("dataSource");
        dataService = new DbDataService(ds);

        Map<String,String> names = new HashMap<>();
        names.put("en", "DD-en");
        names.put("ja", "DD-ja");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "DD-en");
        descr.put("ja", "DD-ja");
        GroupDTO xGroup = new GroupDTO(0, GroupType.PLACE_REP, names, descr, true, new HashSet<Integer>(Arrays.asList(111, 122)));

        GroupDTO newGroup = dataService.create(xGroup, "wjohnson000");
        printIt(newGroup);

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
