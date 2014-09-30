package std.wlj.db.refactor;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestGroup {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        DbDataService dbService = null;

        try {
            System.out.println("Setting up services ...");
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            dbService = new DbDataService(ds);

            System.out.println(">>> A PLACE_REP Group ...");
            GroupBridge groupB = dbService.getGroupById(GroupBridge.TYPE.PLACE_REP, 55);
            PrintUtil.printIt(groupB);

            System.out.println(">>> A PLACE_TYPE Group ...");
            groupB = dbService.getGroupById(GroupBridge.TYPE.PLACE_TYPE, 60);
            PrintUtil.printIt(groupB);

            System.out.println(">>> Non-existent Group ...");
            groupB = dbService.getGroupById(GroupBridge.TYPE.PLACE_TYPE, 55);
            PrintUtil.printIt(groupB);

            System.out.println(">>> All groups ...");
            for (GroupBridge groupBB : dbService.getGroups(GroupBridge.TYPE.PLACE_REP)) {
                PrintUtil.printIt(groupBB);
            }

            System.out.println(">>> PLACE_REP groups for <8> ...");
            for (GroupBridge groupBB : dbService.getGroupsByMemberId(GroupBridge.TYPE.PLACE_REP, 8)) {
                PrintUtil.printIt(groupBB);
            }

            System.out.println(">>> PLACE_TYPE groups for <8> ...");
            for (GroupBridge groupBB : dbService.getGroupsByMemberId(GroupBridge.TYPE.PLACE_TYPE, 8)) {
                PrintUtil.printIt(groupBB);
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            dbService.shutdown();
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
