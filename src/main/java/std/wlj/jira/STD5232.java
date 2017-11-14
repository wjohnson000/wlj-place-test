package std.wlj.jira;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;

import std.wlj.ws.rawhttp.HttpHelper;


public class STD5232 {

    /** Base URL of the application */
    private static String masterUrl = "https://place-solr-dev.dev.fsglobal.org/int-solr-55/places/query";
//    private static String masterUrl = "https://beta.familysearch.org/int-solr/places/query";
//    private static String masterUrl = "http://localhost:8080/solr-710/places/select";
//    private static String masterUrl = "http://localhost:8080/solr-710/places/query";

    private static String solrQuery =
        "q=( names:canada )&rows=3&fq=published:1 -deleteId:[* TO *]";

    private static String solrQueryRankedWorks =
        "q=(names:canada)" +
        "&rq={!rerank reRankQuery=$rqq reRankDocs=10000 reRankWeight=12.0}" +
        "&rqq=(type:(173 198 200 209 210 215 246 254 270 278 301 323 343 362 375 520 521))" +
        "&rows=3" +
        "&fq=published:1 -deleteId:[* TO *]" +
//        "&wt=json" +
        "";

    private static String solrQueryRanked =
        "q=(names:canada)" +
        "&rq={!rerank reRankQuery=$rqq reRankDocs=10000 reRankWeight=3}" +
        "&rqq=(type:173 OR type:198 OR type:200 OR type:209 OR type:210 OR type:215 OR type:246 " +
                       "OR type:254 OR type:270 OR type:278 OR type:301 OR type:323 OR type:343 " + 
                       "OR type:362 OR type:375 OR type:520 OR type:521)" +
        "&rows=10" +
        "&fq=published:1 -deleteId:[* TO *]" +
        "&fl=*,score" +
        "&debugQuery=true" +
//        "&wt=xml" +
        "";

    public static void main(String[] args) throws Exception {
//        runSolrQuery(solrQuery);
        runSolrQuery(solrQueryRanked);
    }

    static void runSolrQuery(String query) throws Exception {
        URL url = new URL(masterUrl + "?" + query);
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }
}
