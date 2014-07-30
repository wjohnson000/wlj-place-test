package std.wlj.extxref;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.ExternalXrefDTO;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import std.wlj.util.SolrManager;


public class CreateExtXref {

    private static final Logger logger = new Logger(CreateExtXref.class);


    @SuppressWarnings("resource")
    public static void main(String... args) {

        ApplicationContext appContext = null;
        PlaceDataServiceImpl service = null;
        try {
            logger.info("Setting up services ...");
            SolrDataService solrService = SolrManager.getLocalHttp();

            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbDataService dbService = new DbDataService(ds);

            service = new PlaceDataServiceImpl(dbService, solrService);
            List<ExternalXrefDTO> xrefList =service.createOrUpdate("NGA", "123-wlj", Arrays.asList(5, 15, 20));
            logger.info("Success ... count: " + xrefList.size());
        } catch(PlaceDataException ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            logger.info("Shutting down ...");
            if (service != null) service.shutdown();
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
