/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;

/**
 * Test the new "confirmIfParents(...)" method ...
 * 
 * @author wjohnson000
 *
 */
public class TestConfirmIfParents {

    public static void main(String...arg) {
//        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        SolrService  solrService = SolrManager.awsDevService(true);
        List<Integer> parents = solrService.confirmIfParent(Arrays.asList(1, 11, 111, 1_111, 11_111, 111_111));
        parents.forEach(System.out::println);
        solrService.shutdown();
    }
}
