package std.wlj.dbnew;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GroupDbServiceTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost-wlj.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbRService = new DbReadableService(ds);
            DbWritableService dbWService = new DbWritableService(ds);

            System.out.println("\nALL [TYPE]........................................\n");
            Set<GroupBridge> groupBs = dbRService.getGroups(GroupBridge.TYPE.PLACE_TYPE, false);
            for (GroupBridge groupB : groupBs) {
                System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
                System.out.println("   M: " + groupB.getDirectMembers());
                System.out.print("   S: ");
                for (GroupBridge subB : groupB.getDirectSubGroups()) {
                    System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
                }
                System.out.println();
            }

            System.out.println("\nALL [PLACE-REP]...................................\n");
            groupBs = dbRService.getGroups(GroupBridge.TYPE.PLACE_REP, false);
            for (GroupBridge groupB : groupBs) {
                System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
                System.out.println("   M: " + groupB.getDirectMembers());
                System.out.print("   S: ");
                for (GroupBridge subB : groupB.getDirectSubGroups()) {
                    System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
                }
            }

            System.out.println("\nMEMBERS [PLACE-REP]...................................\n");
            groupBs = dbRService.getGroupsByMemberId(GroupBridge.TYPE.PLACE_REP, 4, false);
            for (GroupBridge groupB : groupBs) {
                System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
                System.out.println("   M: " + groupB.getDirectMembers());
                System.out.print("   S: ");
                for (GroupBridge subB : groupB.getDirectSubGroups()) {
                    System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
                }
                System.out.println();
            }

            System.out.println("\nONE [TYPE].........................................\n");
            GroupBridge groupB = dbRService.getGroupById(GroupBridge.TYPE.PLACE_TYPE, 4, false);
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();

            System.out.println("\nONE [PLACE-REP].....................................\n");
            groupB = dbRService.getGroupById(GroupBridge.TYPE.PLACE_REP, 80, false);
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();

            System.out.println("\nNEW..................................................\n");
            Set<Integer> members = new HashSet<>(Arrays.asList(2, 3, 4, 5));
            Set<Integer> subGroups = new HashSet<>(Arrays.asList(65, 70, 75));
            groupB = dbWService.createGroup(GroupBridge.TYPE.PLACE_REP, members, subGroups, names, descr, true, "wjohnson000");
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();

            System.out.println("\nUPD..................................................\n");
            members = new HashSet<>(Arrays.asList(4, 5, 6));
            subGroups = new HashSet<>(Arrays.asList(70, 75, 80));
            names.put("ru", "ru-name");
            descr.put("ru", "ru-desc");
            groupB = dbWService.updateGroup(groupB.getGroupId(), GroupBridge.TYPE.PLACE_REP, members, subGroups, names, descr, true, "wjohnson000");
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();

            System.out.println("\nUPD-X................................................\n");
            members = new HashSet<>(Arrays.asList(2, 4, 5));
            subGroups = new HashSet<>(Arrays.asList(65, 75, 80));
            names.remove("fr");
            descr.remove("fr");
            groupB = dbWService.updateGroup(groupB.getGroupId(), GroupBridge.TYPE.PLACE_REP, members, subGroups, names, descr, true, "wjohnson000");
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();

            System.out.println("\nONE [PLACE-REP].....................................\n");
            groupB = dbRService.getGroupById(GroupBridge.TYPE.PLACE_REP, groupB.getGroupId(), false);
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
