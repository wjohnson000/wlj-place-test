package std.wlj.solr;

import org.familysearch.standards.place.data.GroupBridge;

public class AppDataManagerLocalTest {
    public static void main(String... args) {
        AppDataManagerLocal appMgr = new AppDataManagerLocal("C:/temp/app-data");
        appMgr.loadGroups();
        appMgr.loadMemberToGroupMapping();

        GroupBridge bridge14 = appMgr.getGroup(GroupBridge.TYPE.PLACE_REP, 14, false);
        System.out.println("BB-14: " + bridge14);

        GroupBridge bridge34 = appMgr.getGroup(GroupBridge.TYPE.PLACE_REP, 34, false);
        System.out.println("BB-34: " + bridge34);
        if (bridge34 != null) {
            bridge34.getDirectMembers().forEach(System.out::println);
        }
    }
}
