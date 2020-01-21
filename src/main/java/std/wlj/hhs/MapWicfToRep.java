/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.familysearch.standards.place.util.PlaceHelper;


/**
 * WICF (Where [did] I Come From) facts from the "Discovery" team.  De-normalize the data, which was
 * provided in CSV format.  NOTE: some rows span multiple lines!!  NOTE: the "apache-commons" CSV
 * package is used to parse the data, which handles the optional double quotes, multiple-line span,
 * etc.  Yeah!!
 *  
 * @author wjohnson000
 *
 */
public class MapWicfToRep {

    static class CountryInfoX {
        int    wicfId;
        String wicfCode;
        int    repId;
        Map<String, String> names = new TreeMap<>();
    }

    private static final String BASE_DIR  = "C:/D-drive/homelands/WICF/csv";
    private static final String PLACE_DIR = "C:/temp/db-dump";
    private static final String REP_FILE  = "place-rep-all.txt";
    private static final String NAME_FILE = "display-name-all.txt";

    
    private static final Map<Integer, Map<String, String>> trxValues = new TreeMap<>();
    private static final Map<Integer, CountryInfoX> countryData = new TreeMap<>();
    private static final Map<Integer, Integer> id2IdMap = mapWicfToPlace();

    public static void main(String...args) throws Exception {
        loadTranslationData();
        loadCountries();
        matchToPlaceData();
    }

    static void loadTranslationData() throws Exception {
        List<List<String>> trxData = readAll("0-translation-values-data.csv");

        boolean isHdr = true;
        for (List<String> trxDatum : trxData) {
            if (isHdr) {
                isHdr = false;
            } else {
                int key = Integer.parseInt(trxDatum.get(0));
                Map<String, String> trxValue = trxValues.computeIfAbsent(key, kk -> new HashMap<>());
                if (trxValue.containsKey(trxDatum.get(2))) {
                    System.out.println("Duplicate!! " + trxDatum);
                }
                trxValue.put(trxDatum.get(2), trxDatum.get(1));
            }
        }
    }

    static void loadCountries() throws Exception {
        List<List<String>> rowData = readAll("1-country-data.csv");
        rowData.stream()
            .skip(1L)
            .forEach(row -> {
                CountryInfoX ci = makeCountry(row);
                countryData.put(ci.wicfId, ci);
            });
    }

    /**
     * Read all "records" from a file given the name.  Each logical row -- which may span multiple
     * lines -- will be split into individual fields at the comma and optional double-quotes.
     * 
     * @param filename file name, relative to the defined BASE_DIR
     * @return List of logical rows, split into individual fields
     */
    static List<List<String>> readAll(String filename) throws IOException {
        List<List<String>> rowData = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(Paths.get(BASE_DIR, filename), StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            rowData.add(parser.getHeaderNames());

            for (CSVRecord record : parser) {
                List<String> rowD = new ArrayList<>();
                for (String hdr: parser.getHeaderNames()) {
                    rowD.add(record.get(hdr));
                }
                rowData.add(rowD);
            }
        } catch(Exception ex) {
            System.out.println("OOPS!! " + ex.getMessage());
        }

        return rowData;
    }

    static CountryInfoX makeCountry(List<String> rawData) {
        CountryInfoX ci = new CountryInfoX();

        ci.wicfId = Integer.parseInt(rawData.get(0));
        ci.wicfCode = rawData.get(1);
        ci.names = trxValues.get(ci.wicfId);
        ci.repId = id2IdMap.getOrDefault(ci.wicfId, 0);

        return ci;
    }

    static void matchToPlaceData() throws IOException {
        Set<Integer> topRepIds = getTopLevelReps();
        Map<Integer, Set<String>> repNames = getNames(topRepIds);

        Map<String, Set<Integer>> nameToRep = new HashMap<>();
        repNames.entrySet().forEach(entry -> {
            entry.getValue().forEach(name -> {
                Set<Integer> reps = nameToRep.computeIfAbsent(name, kk -> new HashSet<>());
                reps.add(entry.getKey());
            });
        });

        for (CountryInfoX ci : countryData.values()) {
            if (ci.repId == 0) {
                Set<String> tNames = ci.names.values().stream()
                        .map(nn -> PlaceHelper.normalize(nn).toLowerCase())
                        .collect(Collectors.toSet());
                Set<Integer> repIds = new HashSet<>();
                for (String tName : tNames) {
                    repIds.addAll(nameToRep.getOrDefault(tName, Collections.emptySet()));
                }
                
                System.out.println("\n=============================================================================");
                System.out.println("CCC: " + ci.wicfId + " | " + ci.wicfCode + " | " + ci.names);
                if (repIds.size() == 1) {
                    ci.repId = repIds.stream().findFirst().orElse(0);
                    System.out.println("  R: " + ci.repId);
                } else if (repIds.size() > 1) {
                    for (int repId : repIds) {
                        System.out.println("  R: " + repId + " --> " + repNames.get(repId));
                    }
                } else {
                    System.out.println("  R: <no match>");
                }
            }
        }
    }

    static Set<Integer> getTopLevelReps() throws IOException {
        Set<Integer> repIds = new HashSet<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(PLACE_DIR, REP_FILE));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.main: " + lineCnt);

                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 5) {
                    String repId = rowFields[0];
                    String parId = rowFields[2];
                    String delId = rowFields[9];
                    if ((parId.trim().isEmpty()  ||  parId.trim().equals("0"))  &&
                            (delId.trim().isEmpty()  ||  delId.trim().equals("0"))) {
                        repIds.add(Integer.parseInt(repId));
                    }
                }
            }
        }

        return repIds;
    }
    
    static Map<Integer, Set<String>> getNames(Set<Integer> topRepIds) throws IOException {
        Map<Integer, Set<String>> repNames = new HashMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(PLACE_DIR, NAME_FILE));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("NAME.main: " + lineCnt);

                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 4) {
                    String repIdS = rowFields[0];
                    String name   = rowFields[3];
                    try {
                        int repId = Integer.parseInt(repIdS);
                        if (topRepIds.contains(repId)) {
                            Set<String> names = repNames.computeIfAbsent(repId, kk -> new HashSet<>());
                            names.add(PlaceHelper.normalize(name).toLowerCase());
                        }
                    } catch(NumberFormatException ex) {
                        ; // Do nothing ...
                    }
                }
            }
        }

        return repNames;
    }

    static Map<Integer, Integer> mapWicfToPlace() {
        Map<Integer, Integer> idMap = new TreeMap<>();

        idMap.put(1, 303);
        idMap.put(2, 309);
        idMap.put(3, 237);
        idMap.put(4, 93);
        idMap.put(5, 157);
        idMap.put(6, 150);
        idMap.put(7, 56);  // England
        idMap.put(8, 200);
        idMap.put(9, 136);
        idMap.put(10, 66);
        idMap.put(11, 184);
        idMap.put(12, 111);
        idMap.put(13, 32);
        idMap.put(14, 247);
        idMap.put(15, 78);
        idMap.put(16, 308);
        idMap.put(17, 164);
        idMap.put(18, 304);
        idMap.put(19, 225);
        idMap.put(20,  0);  // Reunion Island
        idMap.put(21, 212);
        idMap.put(22, 19);
        idMap.put(20,  0);  // Aland Islands
        idMap.put(24, 51);
        idMap.put(25, 26);
        idMap.put(26, 28);
        idMap.put(27, 203);
        idMap.put(28, 262);
        idMap.put(29, 50);
        idMap.put(30, 224);
        idMap.put(31, 75);
        idMap.put(32, 217);
        idMap.put(33, 146);
        idMap.put(34, 63);
        idMap.put(35, 137);
        idMap.put(36, 13);
        idMap.put(37, 194);
        idMap.put(38, 631);
        idMap.put(39, 110);
        idMap.put(40, 195);
        idMap.put(41, 142);
        idMap.put(42, 73);
        idMap.put(43, 270);  // Bonaire
        idMap.put(44, 213);
        idMap.put(45, 141);
        idMap.put(46, 185);
        idMap.put(47, 214);
        idMap.put(48, 24);
        idMap.put(49, 241);
        idMap.put(50, 258);
        idMap.put(51, 102);
        idMap.put(52, 37);
        idMap.put(53, 201);
        idMap.put(54, 95);  // St. Helena
        idMap.put(55, 35);
        idMap.put(56, 220);
        idMap.put(57, 3887);  // Island in Norway
        idMap.put(58, 168);
        idMap.put(59, 145);
        idMap.put(60, 191);
        idMap.put(61, 30);
        idMap.put(62, 242);
        idMap.put(63, 67);
        idMap.put(64, 133);
        idMap.put(65, 156);
        idMap.put(66, 384415);  // Cocos Island, Guam
        idMap.put(67, 24);   // Sudan, could be 274 or 275
        idMap.put(68, 228);
        idMap.put(69, 178);
        idMap.put(70, 192);
        idMap.put(71, 175);
        idMap.put(72, 228);  // Congo, could also be 239
        idMap.put(73, 10);  // Sint Maarten
        idMap.put(74, 62);
        idMap.put(75, 233);
        idMap.put(76, 257);
        idMap.put(77, 186);
        idMap.put(78, 45);
        idMap.put(79, 159);
        idMap.put(80, 71);
        idMap.put(81, 221);
        idMap.put(82, 131);
        idMap.put(83, 163);
        idMap.put(84, 108);
        idMap.put(85, 162);
        idMap.put(86, 219);
        idMap.put(87, 113);
        idMap.put(88, 189);
        idMap.put(89, 209);
        idMap.put(90, 10904584);  // Curacao (and Dependencies)
        idMap.put(91, 68);
        idMap.put(92, 86);
        idMap.put(93, 49);
        idMap.put(94, 61);
        idMap.put(95, 132);
        idMap.put(96, 119);
        idMap.put(97, 16);  // Timor, could be 15
        idMap.put(98, 60);
        idMap.put(99, 140);
        idMap.put(100, 77);
        idMap.put(101, 138);
        idMap.put(102, 197);
        idMap.put(103, 222);
        idMap.put(104, 190);
        idMap.put(105, 120);
        idMap.put(106, 227);
        idMap.put(107, 59);
        idMap.put(108, 48);
        idMap.put(109, 193);
        idMap.put(110, 65);
        idMap.put(111, 167);
        idMap.put(112, 216);
        idMap.put(113, 29);
        idMap.put(114, 299);
        idMap.put(115, 153);
        idMap.put(116, 1);
        idMap.put(117, 91);
        idMap.put(118, 41);
        idMap.put(119, 125);
        idMap.put(120, 158);
        idMap.put(121, 58);
        idMap.put(122, 31);
        idMap.put(123, 129);
        idMap.put(124, 182);
        idMap.put(125, 134);
        idMap.put(126, 130);
        idMap.put(127, 152);
        idMap.put(128, 96);
        idMap.put(129, 300);
        idMap.put(130, 64);
        idMap.put(131, 235);
        idMap.put(132, 202);
        idMap.put(133, 181);
        idMap.put(134, 84);
        idMap.put(135, 92);
        idMap.put(136, 128);
        idMap.put(137, 204);
        idMap.put(138, 266);
        idMap.put(139, 118);
        idMap.put(140, 187);
        idMap.put(141, 56);  // England or Great Britain, same as #7 above
        idMap.put(142, 179);
        idMap.put(143, 183);
        idMap.put(144, 252);
        idMap.put(145, 85);
        idMap.put(146, 3852);
        idMap.put(147, 230);
        idMap.put(148, 100);
        idMap.put(149, 83);
        idMap.put(150, 180);
        idMap.put(151, 169);
        idMap.put(152, 104);
        idMap.put(153, 229);
        idMap.put(154, 27);
        idMap.put(155, 89);
        idMap.put(156, 174);
        idMap.put(157, 301);
        idMap.put(158, 210);
        idMap.put(159, 155);  // Guyana, could be 154 or 3893
        idMap.put(160, 265);
        idMap.put(161, 20);
        idMap.put(162, 3878);  // Heard and McDonald Island
        idMap.put(163, 173);
        idMap.put(164, 34);
        idMap.put(165, 36);
        idMap.put(166, 69);
        idMap.put(167, 144);
        idMap.put(168, 52);  // Indonesia
        idMap.put(169, 126);  // Mayotte (France)
        idMap.put(170, 208);
        idMap.put(171, 139);
        idMap.put(172, 82);
        idMap.put(173, 232);
        idMap.put(174, 97);
        idMap.put(175, 177);
        idMap.put(176, 74);
        idMap.put(177, 76);
        idMap.put(178, 54);
        idMap.put(179, 33);
        idMap.put(180, 245);
        idMap.put(181, 3853);
        idMap.put(182, 248);  // Zimbabwe, could also be 249, 250
        idMap.put(183, 90);
        idMap.put(184, 151);
        idMap.put(185, 43);
        idMap.put(186, 243);
        idMap.put(187, 251);
        idMap.put(188, 166);
        idMap.put(189, 198);
        idMap.put(190, 231);
        idMap.put(191, 196);
        idMap.put(192, 3299);
        idMap.put(193, 279);
        idMap.put(194, 148);
        idMap.put(195, 103);
        idMap.put(196, 254);
        idMap.put(197, 53);
        idMap.put(198, 147);
        idMap.put(199, 188);
        idMap.put(200, 170);
        idMap.put(201, 240);
        idMap.put(202, 234);
        idMap.put(203, 207);
        idMap.put(204, 55);
        idMap.put(205, 223);
        idMap.put(206, 70);
        idMap.put(207, 72);
        idMap.put(208, 22);
        idMap.put(209, 205);  // Monaco
        idMap.put(210, 246);
        idMap.put(211, 261);
        idMap.put(212, 11);  // St. Martin
        idMap.put(213, 42);
        idMap.put(214, 88);
        idMap.put(215, 143);
        idMap.put(216, 160);
        idMap.put(217, 135);
        idMap.put(218, 253);
        idMap.put(219, 21);
        idMap.put(220, 302);
        idMap.put(221, 3873);  // Martinique (French West Indies)
        idMap.put(222, 206);
        idMap.put(223, 98);
        idMap.put(224, 23);
        idMap.put(225, 199);
        idMap.put(226, 176);
        idMap.put(227, 211);
        idMap.put(228, 218);
        idMap.put(229, 256);  // Malaysia
        idMap.put(230, 38);
        idMap.put(231, 57);
        idMap.put(232, 122);
        idMap.put(233, 161);
        idMap.put(234, 3882);  // Norfolk Island, Australia
        idMap.put(235, 226);
        idMap.put(236, 172);
        idMap.put(237, 236);
        idMap.put(238, 40);
        idMap.put(239, 165);
        idMap.put(240, 171);
        idMap.put(241, 46);
        idMap.put(242, 215);
        idMap.put(243, 149);
        idMap.put(244, 44);
        idMap.put(245, 39);
        idMap.put(246, 3879);
        idMap.put(247, 238);
        idMap.put(248, 255);
        idMap.put(249, 47);
        idMap.put(250, 25);
        idMap.put(251, 3863);
        idMap.put(252, 87);

        return idMap;
    }
}
