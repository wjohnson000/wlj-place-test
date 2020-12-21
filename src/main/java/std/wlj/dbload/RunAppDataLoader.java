/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbload;

import java.util.List;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.loader.reader.AppDataReader;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class RunAppDataLoader {

    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.1.0");
        DbHelper dbHelper = new DbHelper(DbConnectionManager.getDataSourceSams());
        AppDataReader appReader = new AppDataReader(dbHelper);
        List<PlaceRepDoc> appDocs = appReader.getAppDocs("|");
        solrConn.add(appDocs);
        solrConn.commit();
        solrConn.shutdown();
        System.exit(0);
    }
}
