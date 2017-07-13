package std.wlj.analysis.dao.jdbc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.familysearch.standards.analysis.dal.helper.SearchCriteria;
import org.familysearch.standards.analysis.dal.helper.ThreadLocalConnection;
import org.familysearch.standards.analysis.dal.jdbc.impl.SearchResultsJdbcDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbSearchCount;
import org.familysearch.standards.analysis.dal.model.DbSearchResult;

import std.wlj.datasource.DbConnectionManager;

public class SearchResultsDAO {

    public static void main(String...args) throws SQLException {
        DataSource globalDS = DbConnectionManager.getDataSourceWLJ();
        ThreadLocalConnection.init(globalDS);

        SearchResultsJdbcDAOImpl dao = new SearchResultsJdbcDAOImpl();

        Map<String,String> params = new HashMap<>();
        params.put("min-rel", "0");

        SearchCriteria critCrit = new SearchCriteria.Builder()
                .setPlaceName("Linden Hill")
                .setQueryParams(params)
                .setRepId(4645740)
                .setRepId(4645739)
                .build();
        List<DbSearchResult> results = dao.search(critCrit);
        DbSearchCount counts = dao.searchCount(critCrit);
        displayStuff(results, counts);
        ThreadLocalConnection.remove();
    }

    static void displayStuff(List<DbSearchResult> results, DbSearchCount counts) {
        System.out.println("=====================================================================");
        System.out.println("Counts.interp: " + counts.getInterpCount());
        System.out.println("      request: " + counts.getRequestCount());
        System.out.println("       result: " + counts.getResultCount());
        System.out.println("        start: " + counts.getStartDate().getTime());
        System.out.println("          end: " + counts.getEndDate().getTime());

        for (DbSearchResult result : results) {
            System.out.println();
            System.out.println("Request.id: " + result.getRequest().getId());
            System.out.println("      name: " + result.getRequest().getPlaceName());
            System.out.println("    params: " + result.getRequest().getParameters());
            System.out.println(" Result.id: " + result.getResult().getId());
            System.out.println("     count: " + result.getResult().getCount());
            System.out.println("      anns: " + result.getResult().getAnnotations());
            System.out.println("    RepRep: " + result.getReps().stream().map(rep -> String.valueOf(rep.getRepId())).collect(Collectors.joining(",", "[", "]")));
        }
    }
}
