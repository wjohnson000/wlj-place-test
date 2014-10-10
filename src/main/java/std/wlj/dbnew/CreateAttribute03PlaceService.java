package std.wlj.dbnew;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.AttributeDTO;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbReadableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import std.wlj.util.SolrManager;


public class CreateAttribute03PlaceService {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbService = new DbReadableService(ds);
            SolrDataService solrService = SolrManager.getLocalHttp();
            PlaceDataServiceImpl service = new PlaceDataServiceImpl(dbService, solrService);

            AttributeDTO attrDTO = new AttributeDTO(0, 1, 433, 2000, "WLJ-TEST-XXXX", "en", 0);
            AttributeDTO attrDTOX = service.create(attrDTO, "wjohnson");
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
