/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class RunSolrBackup {

    public static void main(String... args) throws Exception {
        try {
            SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.7.1");
            
            SolrQuery query = new SolrQuery("repId:4231034");
            query.setRows(42);
            query.setSort("repId", SolrQuery.ORDER.desc);
            System.out.println("QRY: " + query);
            
            List<PlaceRepDoc> docs = solrConn.search(query);
            System.out.println("CNT: " + docs.size());
//
//            boolean isOK = solrConn.createBackupDumb("wayne", "C:/temp/solr-backup");
//            System.out.println("Backup.OK? " + isOK);
//
//            boolean isOKx = solrConn.listStuff();
//            System.out.println("Backup.OK? " + isOKx);
//
//            boolean isOKy = solrConn.createBackup("wayne", "C:/temp/solr-backup");
//            System.out.println("Backup.OK? " + isOKy);
        } finally {
            System.exit(0);
        }
    }
}
