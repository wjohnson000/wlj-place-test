/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class BaseAll {

    static DbServices dbService = null;
    static SolrService solrService = null;
    static PlaceDataServiceImpl dataService = null;

    static void setupServices() {
        dbService = DbConnectionManager.getDbServicesSams();
        solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        dataService = new PlaceDataServiceImpl(solrService, dbService.readService, dbService.writeService);
    }

    static void shutdownServices() {
        if (dbService != null) dbService.shutdown();
        if (solrService != null) solrService.shutdown();
        if (dataService != null) dataService.shutdown();

        dbService = null;
        solrService = null;
        dataService = null;
}
}
