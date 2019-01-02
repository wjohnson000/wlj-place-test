package std.wlj.db2solr;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Load the following files:
 *    -- parent --> grand-parent
 *    -- child --> delete-id
 * 
 * Then read in the child --> parent file and look for both natural cycles, and cycles
 * involving the delete-id stuff ...
 * 
 * @author wjohnson000
 *
 */
public class ZzzFindCycles02 {

    private static Map<Integer,Integer> repChainMap = new TreeMap<>();
    private static Map<Integer,Integer> delRepIdMap = new TreeMap<>();


    public static void main(String... args) throws Exception {
        loadRepChainMap();
        loadDelRepIdMap();
        checkAllChains();
    }

    private static void loadRepChainMap() throws Exception {
        List<String> data = Files.readAllLines(Paths.get("C:/temp/zzz-rep-chain.txt"), StandardCharsets.UTF_8);

        for (String datum : data) {
            String[] tokens = datum.split("\\|");
            if (tokens.length > 1) {
                repChainMap.put(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
            }
        }

        System.out.println("Rep-Chain size: " + repChainMap.size());
    }

    private static void loadDelRepIdMap() throws Exception {
        List<String> data = Files.readAllLines(Paths.get("C:/temp/zzz-delete-id.txt"), StandardCharsets.UTF_8);

        for (String datum : data) {
            String[] tokens = datum.split("\\|");
            if (tokens.length > 1) {
                delRepIdMap.put(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
            }
        }

        System.out.println("Delete-Id size: " + delRepIdMap.size());
    }

    private static void checkAllChains() throws Exception {
        BufferedReader reader = 
                Files.newBufferedReader(Paths.get("C:/temp/zzz-child-par.txt"), StandardCharsets.UTF_8);
        String line  = null;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\\|");
            if (tokens.length > 1) {
                int repId = Integer.parseInt(tokens[0]);
                int parId = Integer.parseInt(tokens[1]);
                List<Integer> chain01 = getNormalChain(repId, parId);
                List<Integer> chain02 = getDeleteChain(repId, parId);
                if (chain02.size() > 10) {
                    System.out.println(chain01);
                    System.out.println(chain02);
                    System.out.println();
                }
            }
        }
    }

    private static List<Integer> getNormalChain(int repId, int parId) {
        List<Integer> chain = new ArrayList<>();

        chain.add(repId);
        int parIdx = parId;
        while (parIdx > 0) {
            chain.add(parIdx);
            parIdx = repChainMap.containsKey(parIdx) ? repChainMap.get(parIdx) : 0;
        }

        return chain;
    }

    private static List<Integer> getDeleteChain(int repId, int parId) {
        List<Integer> chain = new ArrayList<>();

        int depth = 0;
        int repIdx = repId;
        while (delRepIdMap.containsKey(repIdx)  &&  depth++ < 12) {
            chain.add(-1 * repIdx);
            repIdx = delRepIdMap.get(repIdx);
            if (repIdx == parId) {
                System.out.println("Child deleted to Parent|" + repId + "|" + parId);
            }
        }

        chain.add(repIdx);
        int parIdx = parId;
        while (parIdx > 0  &&  depth++ < 12) {
            if (delRepIdMap.containsKey(parIdx)) {
                chain.add(-1 * parIdx);
                parIdx = delRepIdMap.get(parIdx);
            } else {
                chain.add(parIdx);
                parIdx = repChainMap.containsKey(parIdx) ? repChainMap.get(parIdx) : 0;
            }
        }

        return chain;
    }
}
