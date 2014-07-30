package std.wlj.thread;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class WaitForThread01 {

    private static WaitForThread01 onlyInstance = null;
    private static String currStatus = "new";

    private String      processId = String.valueOf(System.nanoTime());
    private ScheduledExecutorService fScheduler;

    /**
     * Private constructor ... enforced singleton
     */
    private WaitForThread01() { }

    /**
     * Setup and start the DbLoadManager, if not already running ...
     *  
     * @param solrURL SOLR master URL
     * @param dbSource data-source for DB connections
     */
    public static void startDbLoadManager() {
        if (onlyInstance == null) {
            onlyInstance = new WaitForThread01();
            onlyInstance.fScheduler = Executors.newScheduledThreadPool(3);
            onlyInstance.startReplicationCheck();
        }
    }

    /**
     * Start the replication check, which will ask the master for its status to determine
     * if the load process needs to be started.
     */
    private void startReplicationCheck() {
        fScheduler.scheduleWithFixedDelay(
            new Runnable() {
                @Override
                public void run() {
                    replicationCheck();
                }
            },
            3,    // Initial delay = 3 seconds
            10,   // Delay between execution = 10 seconds
            TimeUnit.SECONDS);
    }

    /**
     * Run the replication check, to determine if we need to start the DB load
     * process.
     */
    private void replicationCheck() {
        // Retrieve the current status; if "new", then request that this server be
        // allowed to do the population.  If granted, start the load.
        Map<String,String> response = pingSolrServer("status");
        if ("new".equals(response.get("status"))  ||  "stalled".equals(response.get("status"))) {
            currStatus = "in-progress";
            response = pingSolrServer("start");
            if ("in-progress".equals(response.get("status"))  &&  processId.equals(response.get("procId"))) {
                startDbLoad();
            }
        }
    }

    /**
     * Start the DB load process.  Keep running until either: 1) all documents are loaded,
     * or 2) this stalls out and another process picks it up ...
     */
    private void startDbLoad() {
        // Define the temporary directory where files are to be stored, and the
        // database connectivity parameter defaults
        final String tempDir = System.getProperty("java.io.tmpdir", "/tmp/db-flat-file");

        // Ensure that the directory exists or can be created
        File file = new File(tempDir);
        if (! file.exists()) {
            if (! file.mkdirs()) {
                System.out.println("Unable to create the temporary directory: " + tempDir);
                return;
            }
        } else if (! file.isDirectory()) {
            System.out.println("Temporary 'directory' exists but isn't a directory: " + tempDir);
            return;
        }

        // Start the load process in a separate thread
        Thread loadThread = null;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i=0;  i<64;  i++) {
                    try { Thread.sleep(5000L); } catch(Exception ex) { }
                    System.out.println("     I: " + i);
                }
            }
        };
        loadThread = new Thread(runnable);
        loadThread.start();

        // Wait for the load thread to finish, doing period "commit" calls to let the
        // server know that we're still alive
        boolean isDone = false;
        while (! isDone) {
            if (loadThread.isAlive()) {
                Map<String,String> response = pingSolrServer("commit");
                if ("in-progress".equals(response.get("status"))  &&  processId.equals(response.get("procId"))) {
                    try {
                        Thread.sleep(30000L);
                    } catch (InterruptedException ex) {
                        System.out.println("Couldn't sleep ... " +  ex.getMessage());
                    }
                }
            } else {
                isDone = true;
                currStatus = "ready";
                Map<String,String> response = pingSolrServer("finish");
                System.out.println("Database Load finished -- SOLR status: " + response.get("status"));
            }
        }
    }

    /**
     * Issue a GET request against the replication-status endpoint, returning the
     * current status, process-id and last-update delta.
     * 
     * @param action action to perform
     * @return map w/ "status", "procId" and "lastUpdate" keys
     */
    private Map<String,String> pingSolrServer(String action) {
        System.out.println("Pinging SOLR w/ action ... " + action);

        Map<String,String> results = new HashMap<>();
        results.put("status", currStatus);
        results.put("procId", processId);
        return results;
    }

    /**
     * Get this silly thing a-goin'
     * @param args
     */
    public static void main(String... args) {
        WaitForThread01.startDbLoadManager();
    }
}
