package std.wlj.dbnew;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.AttributeDTO;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CreateAttribute02DbService {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbDataService dbService = new DbDataService(ds);

            AttributeDTO attrDTO = new AttributeDTO(0, 1, 433, 2000, "WLJ-TEST-XXX", "en", 0);
            AttributeDTO attrDTOX = dbService.create(attrDTO, "wjohnson");
            System.out.println("NEW: " + attrDTOX + " --> " + attrDTOX.getId());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
