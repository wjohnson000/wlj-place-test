package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

public class GenSQLForCitationSourceAnalysis {

    static final String baseDir   = "C:/temp";
    static final String citnFile  = "db-citation-all.txt";

    static final Map<Integer, Integer> srcIdFromTo = new HashMap<>();
    static {
        srcIdFromTo.put(119, 1);
        srcIdFromTo.put(440, 1);
        srcIdFromTo.put(12, 3);
        srcIdFromTo.put(15, 3);
        srcIdFromTo.put(16, 3);
        srcIdFromTo.put(17, 3);
        srcIdFromTo.put(25, 3);
        srcIdFromTo.put(27, 3);
        srcIdFromTo.put(34, 3);
        srcIdFromTo.put(37, 3);
        srcIdFromTo.put(38, 3);
        srcIdFromTo.put(39, 3);
        srcIdFromTo.put(42, 3);
        srcIdFromTo.put(47, 3);
        srcIdFromTo.put(61, 3);
        srcIdFromTo.put(64, 3);
        srcIdFromTo.put(70, 3);
        srcIdFromTo.put(72, 3);
        srcIdFromTo.put(83, 3);
        srcIdFromTo.put(86, 3);
        srcIdFromTo.put(90, 3);
        srcIdFromTo.put(94, 3);
        srcIdFromTo.put(98, 3);
        srcIdFromTo.put(99, 3);
        srcIdFromTo.put(104, 3);
        srcIdFromTo.put(108, 3);
        srcIdFromTo.put(112, 3);
        srcIdFromTo.put(113, 3);
        srcIdFromTo.put(114, 3);
        srcIdFromTo.put(130, 3);
        srcIdFromTo.put(151, 3);
        srcIdFromTo.put(152, 3);
        srcIdFromTo.put(169, 3);
        srcIdFromTo.put(363, 3);
        srcIdFromTo.put(372, 3);
        srcIdFromTo.put(423, 3);
        srcIdFromTo.put(455, 3);
        srcIdFromTo.put(22, 4);
        srcIdFromTo.put(165, 4);
        srcIdFromTo.put(201, 4);
        srcIdFromTo.put(348, 4);
        srcIdFromTo.put(359, 4);
        srcIdFromTo.put(516, 4);
        srcIdFromTo.put(629, 4);
        srcIdFromTo.put(46, 5);
        srcIdFromTo.put(66, 5);
        srcIdFromTo.put(91, 5);
        srcIdFromTo.put(115, 5);
        srcIdFromTo.put(123, 5);
        srcIdFromTo.put(129, 5);
        srcIdFromTo.put(141, 5);
        srcIdFromTo.put(156, 5);
        srcIdFromTo.put(170, 5);
        srcIdFromTo.put(175, 5);
        srcIdFromTo.put(179, 5);
        srcIdFromTo.put(204, 5);
        srcIdFromTo.put(209, 5);
        srcIdFromTo.put(354, 5);
        srcIdFromTo.put(382, 5);
        srcIdFromTo.put(410, 5);
        srcIdFromTo.put(436, 5);
        srcIdFromTo.put(451, 5);
        srcIdFromTo.put(488, 5);
        srcIdFromTo.put(514, 5);
        srcIdFromTo.put(609, 5);
        srcIdFromTo.put(1114, 5);
        srcIdFromTo.put(1134, 5);
        srcIdFromTo.put(1262, 5);
        srcIdFromTo.put(1263, 5);
        srcIdFromTo.put(59, 6);
        srcIdFromTo.put(403, 6);
        srcIdFromTo.put(421, 6);
        srcIdFromTo.put(930, 6);
        srcIdFromTo.put(3, 7);
        srcIdFromTo.put(4, 7);
        srcIdFromTo.put(5, 7);
        srcIdFromTo.put(6, 7);
        srcIdFromTo.put(7, 7);
        srcIdFromTo.put(8, 7);
        srcIdFromTo.put(9, 7);
        srcIdFromTo.put(11, 7);
        srcIdFromTo.put(13, 7);
        srcIdFromTo.put(23, 7);
        srcIdFromTo.put(24, 7);
        srcIdFromTo.put(29, 7);
        srcIdFromTo.put(32, 7);
        srcIdFromTo.put(36, 7);
        srcIdFromTo.put(54, 7);
        srcIdFromTo.put(57, 7);
        srcIdFromTo.put(81, 7);
        srcIdFromTo.put(84, 7);
        srcIdFromTo.put(92, 7);
        srcIdFromTo.put(103, 7);
        srcIdFromTo.put(135, 7);
        srcIdFromTo.put(145, 7);
        srcIdFromTo.put(150, 7);
        srcIdFromTo.put(153, 7);
        srcIdFromTo.put(168, 7);
        srcIdFromTo.put(202, 7);
        srcIdFromTo.put(218, 7);
        srcIdFromTo.put(233, 7);
        srcIdFromTo.put(349, 7);
        srcIdFromTo.put(365, 7);
        srcIdFromTo.put(365, 7);
        srcIdFromTo.put(366, 7);
        srcIdFromTo.put(368, 7);
        srcIdFromTo.put(375, 7);
        srcIdFromTo.put(376, 7);
        srcIdFromTo.put(381, 7);
        srcIdFromTo.put(388, 7);
        srcIdFromTo.put(398, 7);
        srcIdFromTo.put(400, 7);
        srcIdFromTo.put(401, 7);
        srcIdFromTo.put(433, 7);
        srcIdFromTo.put(438, 7);
        srcIdFromTo.put(552, 7);
        srcIdFromTo.put(772, 7);
        srcIdFromTo.put(923, 7);
        srcIdFromTo.put(1287, 7);
        srcIdFromTo.put(31, 9);
        srcIdFromTo.put(51, 9);
        srcIdFromTo.put(60, 9);
        srcIdFromTo.put(63, 9);
        srcIdFromTo.put(68, 9);
        srcIdFromTo.put(101, 9);
        srcIdFromTo.put(102, 9);
        srcIdFromTo.put(106, 9);
        srcIdFromTo.put(116, 9);
        srcIdFromTo.put(122, 9);
        srcIdFromTo.put(131, 9);
        srcIdFromTo.put(132, 9);
        srcIdFromTo.put(164, 9);
        srcIdFromTo.put(184, 9);
        srcIdFromTo.put(187, 9);
        srcIdFromTo.put(207, 9);
        srcIdFromTo.put(378, 9);
        srcIdFromTo.put(389, 9);
        srcIdFromTo.put(399, 9);
        srcIdFromTo.put(435, 9);
        srcIdFromTo.put(568, 9);
        srcIdFromTo.put(733, 9);
        srcIdFromTo.put(749, 9);
        srcIdFromTo.put(773, 9);
        srcIdFromTo.put(941, 9);
        srcIdFromTo.put(2, 10);
        srcIdFromTo.put(105, 10);
        srcIdFromTo.put(124, 10);
        srcIdFromTo.put(138, 10);
        srcIdFromTo.put(139, 10);
        srcIdFromTo.put(355, 10);
        srcIdFromTo.put(356, 10);
        srcIdFromTo.put(364, 10);
        srcIdFromTo.put(370, 10);
        srcIdFromTo.put(374, 10);
        srcIdFromTo.put(422, 10);
        srcIdFromTo.put(477, 10);
        srcIdFromTo.put(483, 10);
        srcIdFromTo.put(506, 10);
        srcIdFromTo.put(537, 10);
        srcIdFromTo.put(544, 10);
        srcIdFromTo.put(550, 10);
        srcIdFromTo.put(1445, 10);
        srcIdFromTo.put(1446, 10);
        srcIdFromTo.put(1447, 10);
        srcIdFromTo.put(1448, 10);
        srcIdFromTo.put(1450, 10);
        srcIdFromTo.put(1451, 10);
        srcIdFromTo.put(1455, 10);
        srcIdFromTo.put(1456, 10);
        srcIdFromTo.put(1457, 10);
        srcIdFromTo.put(1458, 10);
        srcIdFromTo.put(1460, 10);
        srcIdFromTo.put(1461, 10);
        srcIdFromTo.put(1462, 10);
        srcIdFromTo.put(1463, 10);

        srcIdFromTo.put(1, 0);
        srcIdFromTo.put(30, 0);
        srcIdFromTo.put(586, 0);
    }

    public static void main(String... args) throws FileNotFoundException, IOException {
        Set<Integer> repIdsToChange = new HashSet<>();
        Set<Integer> repsWithDeleteOnly = new HashSet<>();
        Map<Integer, Integer> srcIdCount = new TreeMap<>();

        // First Pass
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("Citn.01.Lines: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 8) {
                    String sRepId  = chunks[0];
                    String sSrcId  = chunks[3];

                    int repId = Integer.parseInt(sRepId);
                    repIdsToChange.add(repId);

                    int srcId = Integer.parseInt(sSrcId);
                    Integer xCnt = srcIdCount.getOrDefault(srcId, 0);
                    srcIdCount.put(srcId, xCnt+1);

                    if (srcId == 1  ||  srcId == 30  ||  srcId == 586) {
                        repsWithDeleteOnly.add(repId);
                    }
                }
            }
        }

        // Second Pass
        lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("Citn.02.Lines: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 8) {
                    String sRepId  = chunks[0];
                    String sSrcId  = chunks[3];

                    int repId = Integer.parseInt(sRepId);
                    int srcId = Integer.parseInt(sSrcId);
                    if (srcId != 1  &&  srcId != 30  &&  srcId != 586) {
                        repsWithDeleteOnly.remove(repId);
                    }
                }
            }
        }

        int changeCnt = 0;
        System.out.println();
        for (Integer srcId : srcIdCount.keySet()) {
            String prefix = "    ";
            if (srcIdFromTo.keySet().contains(srcId)) {
                prefix = ">>> ";
                changeCnt += srcIdCount.get(srcId);
            }
            System.out.println(prefix + "|" + srcId + "|" + srcIdCount.get(srcId));
        }

        System.out.println();
        System.out.println("Reps-to-change: " + repIdsToChange.size());
        System.out.println("Change-count: " + changeCnt);
        System.out.println("Reps-in-danger: " + repsWithDeleteOnly.size());
        repsWithDeleteOnly.forEach(repId -> System.out.println("  " + repId));
    }
}
