package std.wlj.analysis.dao;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.analysis.dal.dao.DAOFactory;
import org.familysearch.standards.analysis.dal.dao.SearchResultDAO;
import org.familysearch.standards.analysis.dal.helper.SearchCriteria;
import org.familysearch.standards.analysis.dal.impl.DAOFactoryImpl;
import org.familysearch.standards.analysis.dal.model.DbResultRep;
import org.familysearch.standards.analysis.dal.model.DbSearchResult;

import std.wlj.datasource.DbConnectionManager;

public class SearchResultDAORead {
    public static void main(String...args) {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceWLJ());
        SearchResultDAO searchDAO = daoFactory.getSearchResultDAO();

        Map<String,String> qParams = new HashMap<>();
        qParams.put("is-published", "PUB_ONLY");
        qParams.put("min-rel", "0");

        SearchCriteria criteria = new SearchCriteria.Builder()
                .setPlaceName("Peabody, Mass.")
                .setAcceptLang("en")
                .setUserAgent("wjohnson000")
                .setCollectionId("coll-01")
                .setRepId(4612910)
                .setRelevanceScore(98)
                .setAnnotations(Arrays.asList("TOKEN_NOT_A_PLACE_NAME", "POSSIBLE_MISSING_PLACE"))
                .setStartDate(Calendar.getInstance())
                .setEndDate(Calendar.getInstance())
                .setQueryParams(qParams)
                .setPageNumber(1)
                .setPageSize(25)
                .build();

        List<DbSearchResult> searchResults = searchDAO.search(criteria);
        for (DbSearchResult dbResult : searchResults) {
            System.out.println("=======================================================");
            System.out.println("REQ: " + dbResult.getRequest().getId() + " :: " + dbResult.getRequest().getMd5Hash());
            System.out.println("     " + dbResult.getRequest().getPlaceName());
            System.out.println("     " + dbResult.getRequest().getParameters());
            System.out.println("RES: " + dbResult.getResult().getId() + " :: " + dbResult.getResult().getMd5Hash());
            System.out.println("     " + dbResult.getResult().getAnnotations());
            for (DbResultRep dbRep : dbResult.getResultReps()) {
                System.out.println("REP: " + dbRep.getRepId() + " :: " + dbRep.getRelevanceScore());
                System.out.println("     " + dbRep.getParsePath());
            }
        }
    }
}
