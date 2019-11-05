package std.wlj.access;

import java.util.Date;
import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.CitationBatch;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;


public class TestCitationSIngleVsBatched {

    public static void main(String... args) {
        PlaceDataServiceImpl dataService = null;
        DbServices dbServices = null;
        SolrService solrService = null;

        try {
            int repId = 3331;
            long time0, time1;

            dbServices = DbConnectionManager.getDbServicesSams();
            solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            PlaceRepBridge placeRepB = dbServices.readService.getRep(repId);
            List<CitationBridge> citns = placeRepB.getAllCitations();

            System.out.println("=========================================================================================");
            System.out.println("PR: " + placeRepB);
            citns.forEach(citn -> System.out.println(citn.getCitationId() + " . " + citn.getType().getCode() + " . " +  citn.getSource().getTitle() + " . " + citn.getSourceRef()));
            System.out.println("=========================================================================================");

//            time0 = System.nanoTime();
//            addCitnsBatchy(dataService, repId);
//            time1 = System.nanoTime();
//
//            System.out.println("=========================================================================================");
//            System.out.println("Batch.time=" + (time1-time0)/1_000_000.0);
//            System.out.println("PR: " + placeRepB);
//            citns.forEach(citn -> System.out.println(citn.getCitationId() + " . " + citn.getType().getCode() + " . " +  citn.getSource().getTitle() + " . " + citn.getSourceRef()));
//            System.out.println("=========================================================================================");
//
//            time0 = System.nanoTime();
//            addCitnsSingly(dataService, repId);
//            time1 = System.nanoTime();
//
//            System.out.println("=========================================================================================");
//            System.out.println("Single.time=" + (time1-time0)/1_000_000.0);
//            System.out.println("PR: " + placeRepB);
//            citns.forEach(citn -> System.out.println(citn.getCitationId() + " . " + citn.getType().getCode() + " . " +  citn.getSource().getTitle() + " . " + citn.getSourceRef()));
//            System.out.println("=========================================================================================");
//
//            time0 = System.nanoTime();
//            addCitnsBatchy(dataService, repId);
//            time1 = System.nanoTime();
//
//            System.out.println("=========================================================================================");
//            System.out.println("Batch.time=" + (time1-time0)/1_000_000.0);
//            System.out.println("PR: " + placeRepB);
//            citns.forEach(citn -> System.out.println(citn.getCitationId() + " . " + citn.getType().getCode() + " . " +  citn.getSource().getTitle() + " . " + citn.getSourceRef()));
//            System.out.println("=========================================================================================");
//
//            time0 = System.nanoTime();
//            addCitnsSingly(dataService, repId);
//            time1 = System.nanoTime();
//
//            System.out.println("=========================================================================================");
//            System.out.println("Single.time=" + (time1-time0)/1_000_000.0);
//            System.out.println("PR: " + placeRepB);
//            citns.forEach(citn -> System.out.println(citn.getCitationId() + " . " + citn.getType().getCode() + " . " +  citn.getSource().getTitle() + " . " + citn.getSourceRef()));
//            System.out.println("=========================================================================================");
            
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dataService != null) dataService.shutdown();
            if (dbServices != null) dbServices.shutdown();
            if (solrService != null) solrService.shutdown();
        }

        System.exit(0);
    }

    static void addCitnsSingly(PlaceDataServiceImpl dataService, int repId) throws PlaceDataException {
        for (int ndx=0;  ndx<12;  ndx++) {
            dataService.createCitation(repId, 460, 5, new Date(), null, "source-ref-single-"+ndx, "wjohnson000", null);
        }
    }

    static void addCitnsBatchy(PlaceDataServiceImpl dataService, int repId) throws PlaceDataException {
        CitationBatch batch = dataService.openCitationBatch(false);
        for (int ndx=0;  ndx<12;  ndx++) {
            batch.addForCreate(repId, 460, 5, new Date(), null, "source-ref-batch-"+ndx, null);
        }
        dataService.processCitationBatch(batch, "wjohnson000");
    }
}
