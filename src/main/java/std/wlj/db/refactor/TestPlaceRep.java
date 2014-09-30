package std.wlj.db.refactor;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestPlaceRep {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        DbDataService dbService = null;

        try {
            System.out.println("Setting up services ...");
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            dbService = new DbDataService(ds);

            SearchParameters params = new SearchParameters();
            SearchParameter param01 = SearchParameter.PlaceRepParam.createParam(4);
            SearchParameter param02 = SearchParameter.PlaceRepParam.createParam(9);
            SearchParameter param03 = SearchParameter.PlaceRepParam.createParam(555);
            params.addParam(param01).addParam(param02).addParam(param03);

            System.out.println(">>> Two place-reps ...");
            PlaceSearchResults results = dbService.search(params);
            if (results.getResults() != null) {
                for (PlaceRepBridge placeRepB : results.getResults()) {
                    PrintUtil.printIt(placeRepB);
                }
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
