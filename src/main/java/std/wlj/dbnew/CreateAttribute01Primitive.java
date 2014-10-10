package std.wlj.dbnew;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.dao.dbimpl.RepAttributeDAOImpl;
import org.familysearch.standards.place.dao.model.DbRepAttribute;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CreateAttribute01Primitive {

    private static final Logger logger = new Logger(CreateAttribute01Primitive.class);

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            logger.info("Setting up services ...");
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            RepAttributeDAOImpl attrDAO = new RepAttributeDAOImpl(ds);

            DbRepAttribute repAttr = new DbRepAttribute();
            repAttr.setRepId(1);
            repAttr.setAttrTypeId(433);
            repAttr.setValue("WLJ-TEST");
            repAttr.setYear(2000);
            repAttr.setLocale(StdLocale.ENGLISH);

            DbRepAttribute repAttrC = attrDAO.create(repAttr);
            logger.info("New RepAttr: " + repAttrC + " --> " + repAttrC.getId());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            logger.info("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
