package std.wlj.jira;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;

import std.wlj.ws.rawhttp.HttpHelper;


public class Find10734614 {

    /** Base URL of the application */
    private static String masterUrl = "https://www.familysearch.org/int-solr/places/query";

    private static String solrQuery =
        "q=( repId:10734614 )&rows=3";

    public static void main(String[] args) throws Exception {
        runSolrQuery(solrQuery);
    }

    static void runSolrQuery(String query) throws Exception {
        URL url = new URL(masterUrl + "?" + query);
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }
}
