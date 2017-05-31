package std.wlj.dbnew;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.familysearch.standards.place.data.GroupBridge;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;

public class GroupDbServiceTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

        DbServices dbServices = null;
        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();

            System.out.println("\nALL [TYPE]........................................\n");
            Set<GroupBridge> groupBs = dbServices.readService.getGroups(GroupBridge.TYPE.PLACE_TYPE, false);
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
            groupBs = dbServices.readService.getGroups(GroupBridge.TYPE.PLACE_REP, false);
            for (GroupBridge groupB : groupBs) {
                System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
                System.out.println("   M: " + groupB.getDirectMembers());
                System.out.print("   S: ");
                for (GroupBridge subB : groupB.getDirectSubGroups()) {
                    System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
                }
            }

            System.out.println("\nMEMBERS [PLACE-REP]...................................\n");
            groupBs = dbServices.readService.getGroupsByMemberId(GroupBridge.TYPE.PLACE_REP, 4, false);
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
            GroupBridge groupB = dbServices.readService.getGroupById(GroupBridge.TYPE.PLACE_TYPE, 4, false);
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();

            System.out.println("\nONE [PLACE-REP].....................................\n");
            groupB = dbServices.readService.getGroupById(GroupBridge.TYPE.PLACE_REP, 80, false);
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
            groupB = dbServices.writeService.createGroup(GroupBridge.TYPE.PLACE_REP, members, subGroups, names, descr, true, "wjohnson000", null);
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
            groupB = dbServices.writeService.updateGroup(groupB.getGroupId(), GroupBridge.TYPE.PLACE_REP, members, subGroups, names, descr, true, "wjohnson000", null);
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
            groupB = dbServices.writeService.updateGroup(groupB.getGroupId(), GroupBridge.TYPE.PLACE_REP, members, subGroups, names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
            System.out.println("   M: " + groupB.getDirectMembers());
            System.out.print("   S: ");
            for (GroupBridge subB : groupB.getDirectSubGroups()) {
                System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
            }
            System.out.println();

            System.out.println("\nONE [PLACE-REP].....................................\n");
            groupB = dbServices.readService.getGroupById(GroupBridge.TYPE.PLACE_REP, groupB.getGroupId(), false);
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
            if (dbServices != null) dbServices.shutdown();
        }

        System.exit(0);
    }
}
