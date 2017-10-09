package std.wlj.access;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;

public class GroupTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

      PlaceDataServiceImpl dataService = null;
      DbServices dbServices = null;
      SolrService solrService = null;

      try {
          dbServices = DbConnectionManager.getDbServicesSams();
          solrService = SolrManager.localHttpService();
          dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

          System.out.println("\nALL [TYPE]........................................\n");
          Set<GroupBridge> groupBs = dataService.getGroups(GroupBridge.TYPE.PLACE_TYPE);
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
          groupBs = dataService.getGroups(GroupBridge.TYPE.PLACE_REP);
          for (GroupBridge groupB : groupBs) {
              System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
              System.out.println("   M: " + groupB.getDirectMembers());
              System.out.print("   S: ");
              for (GroupBridge subB : groupB.getDirectSubGroups()) {
                  System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
              }
          }

          System.out.println("\nMEMBERS [PLACE-REP]...................................\n");
          groupBs = dataService.getGroupsByMemberId(GroupBridge.TYPE.PLACE_REP, 4);
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
          GroupBridge groupB = dataService.getGroupById(GroupBridge.TYPE.PLACE_TYPE, 4);
          System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
          System.out.println("   M: " + groupB.getDirectMembers());
          System.out.print("   S: ");
          for (GroupBridge subB : groupB.getDirectSubGroups()) {
              System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
          }
          System.out.println();

//          System.out.println("\nONE [PLACE-REP].....................................\n");
//          groupB = dataService.getGroupById(GroupBridge.TYPE.PLACE_REP, 80);
//          System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
//          System.out.println("   M: " + groupB.getDirectMembers());
//          System.out.print("   S: ");
//          for (GroupBridge subB : groupB.getDirectSubGroups()) {
//              System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
//          }
//          System.out.println();

          System.out.println("\nNEW..................................................\n");
          Set<Integer> members = new HashSet<>(Arrays.asList(2, 3, 4, 5));
          Set<Integer> subGroups = new HashSet<>(Arrays.asList(2, 4, 6));
          groupB = dataService.createGroup(GroupBridge.TYPE.PLACE_TYPE, members, subGroups, names, descr, true, "wjohnson000", null);
          System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
          System.out.println("   M: " + groupB.getDirectMembers());
          System.out.print("   S: ");
          for (GroupBridge subB : groupB.getDirectSubGroups()) {
              System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
          }
          System.out.println();

          System.out.println("\nUPD..................................................\n");
          members = new HashSet<>(Arrays.asList(4, 5, 6));
          subGroups = new HashSet<>(Arrays.asList(4, 6, 8, 10));
          names.put("ru", "ru-name");
          descr.put("ru", "ru-desc");
          groupB = dataService.updateGroup(groupB.getGroupId(), GroupBridge.TYPE.PLACE_TYPE, members, subGroups, names, descr, true, "wjohnson000", null);
          System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
          System.out.println("   M: " + groupB.getDirectMembers());
          System.out.print("   S: ");
          for (GroupBridge subB : groupB.getDirectSubGroups()) {
              System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
          }
          System.out.println();

          System.out.println("\nUPD-X................................................\n");
          members = new HashSet<>(Arrays.asList(2, 4, 5));
          subGroups = new HashSet<>(Arrays.asList(2, 4, 10));
          names.remove("fr");
          descr.remove("fr");
          groupB = dataService.updateGroup(groupB.getGroupId(), GroupBridge.TYPE.PLACE_TYPE, members, subGroups, names, descr, true, "wjohnson000", null);
          System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
          System.out.println("   M: " + groupB.getDirectMembers());
          System.out.print("   S: ");
          for (GroupBridge subB : groupB.getDirectSubGroups()) {
              System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
          }
          System.out.println();

          System.out.println("\nONE [PLACE-TYPE].....................................\n");
          groupB = dataService.getGroupById(GroupBridge.TYPE.PLACE_TYPE, groupB.getGroupId());
          System.out.println("TYPE: " + groupB.getGroupId() + " :: " + groupB.isPublished() + " :: " + groupB.getNames());
          System.out.println("   M: " + groupB.getDirectMembers());
          System.out.print("   S: ");
          for (GroupBridge subB : groupB.getDirectSubGroups()) {
              System.out.print(subB.getGroupId() + "." + subB.getType() + "   ");
          }
          System.out.println();
      } catch(Exception ex) {
          System.out.println("Ex: " + ex.getMessage());
          ex.printStackTrace();
      } finally {
          System.out.println("Shutting down ...");
          if (dataService != null) dataService.shutdown();
          if (dbServices != null) dbServices.shutdown();
          if (solrService != null) solrService.shutdown();
      }

      System.exit(0);
    }
}
