package std.wlj.flatfile;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;






import org.familysearch.standards.place.util.flatfile.FileResultSet;


public class AnalyzePlaceNames {

    /**
     * Store information about a particular name ...
     * @author wjohnson000
     */
    private static class NameData {
        int repId;
        int placeId;
        int typeId;
        String text;
        String locale;
        String typeCode;
        int priority;
    }

    /** Mapping from type-id --> type-code */
    private static Map<Integer,String> typeMap = new TreeMap<Integer,String>();
    static {
        typeMap.put(1025, "NGA_SHORT");
        typeMap.put(1026, "NGA_GENERC");
        typeMap.put(1027, "NGA_SORT");
        typeMap.put(1028, "NGA_FULLN");
        typeMap.put(1029, "NGA_FULLND");
        typeMap.put(1030, "NGA_FULLC");
        typeMap.put(1031, "NGA_FULLV");
        typeMap.put(1032, "NGA_FULLD");
        typeMap.put(1033, "US_STABRV");
        typeMap.put(1034, "OLD_STABRV");
        typeMap.put(1035, "ABRV");
        typeMap.put(1036, "DSPLY");
        typeMap.put(1037, "ISOCNTRYCD");
        typeMap.put(1038, "ISO2LCD");
        typeMap.put(1039, "ISO3LCD");
        typeMap.put(1040, "ISONAME");
        typeMap.put(1041, "LDS_5LTMPL");
        typeMap.put(1042, "LDS_2LTMPL");
        typeMap.put(1043, "LDS_OTHER");
        typeMap.put(1044, "ODM_STD");
        typeMap.put(1045, "ODM_VAR");
        typeMap.put(1046, "NGA_USQUAL");
        typeMap.put(1047, "ETHNIC");
        typeMap.put(1048, "UND");
        typeMap.put(1049, "COMMON");
    }

    /** Mapping from type-code --> priority */
    private static Map<String,Integer> priorityMap = new TreeMap<String,Integer>();
    static {
        priorityMap.put("NGA_SHORT", 4);
        priorityMap.put("NGA_GENERC", 2);
        priorityMap.put("NGA_SORT", 2);
        priorityMap.put("NGA_FULLN", 3);
        priorityMap.put("NGA_FULLND", 2);
        priorityMap.put("NGA_FULLC", 3);
        priorityMap.put("NGA_FULLV", 2);
        priorityMap.put("NGA_FULLD", 1);
        priorityMap.put("US_STABRV", 8);
        priorityMap.put("OLD_STABRV", 8);
        priorityMap.put("ABRV", 4);
        priorityMap.put("DSPLY", 5);
        priorityMap.put("ISOCNTRYCD", 7);
        priorityMap.put("ISO2LCD", 7);
        priorityMap.put("ISO3LCD", 7);
        priorityMap.put("ISONAME", 7);
        priorityMap.put("LDS_5LTMPL", 1);
        priorityMap.put("LDS_2LTMPL", 1);
        priorityMap.put("LDS_OTHER", 1);
        priorityMap.put("ODM_STD", 2);
        priorityMap.put("ODM_VAR", 1);
        priorityMap.put("NGA_USQUAL", 4);
        priorityMap.put("ETHNIC", 1);
        priorityMap.put("UND", 1);
        priorityMap.put("COMMON", 10);
    }


    public static void main(String[] args) throws SQLException {
        File aFile = new File("C:/temp/flat-filex/place-name.txt");
        FileResultSet  nameRS = null;
        nameRS = new FileResultSet();
        nameRS.setSeparator("\\|");
        nameRS.openFile(aFile);

        // Read the names by type ...
        int rowCnt = 0;
        int currRepId = -1;
        Map<Integer,List<NameData>> repNames = new TreeMap<Integer,List<NameData>>();
        Map<Integer,Integer> countByType = new TreeMap<Integer,Integer>();
        Map<Integer,Integer> omitByType = new TreeMap<Integer,Integer>();

        while (nameRS.next()) {
            rowCnt++;
            if (rowCnt % 1000000 == 0) System.out.println("Name-Count: " + rowCnt);

            NameData nameData = new NameData();
            nameData.repId = nameRS.getInt("rep_id");
            nameData.placeId = nameRS.getInt("place_id");
            nameData.typeId = nameRS.getInt("type_id");
            nameData.text = nameRS.getString("text");
            nameData.locale = nameRS.getString("locale");

            /** Adjust this for the correct name-type identifiers */
            nameData.typeId = nameData.typeId + 6;

            nameData.typeCode = typeMap.get(nameData.typeId);
            nameData.priority = priorityMap.get(nameData.typeCode);

            Integer count = countByType.get(nameData.typeId);
            count = (count == null) ? 1 : count+1;
            countByType.put(nameData.typeId, count);

            if (nameData.repId == currRepId) {
                int tPriority = -1 * nameData.priority;
                List<NameData> repName = repNames.get(tPriority);
                if (repName == null) {
                    repName = new ArrayList<NameData>();
                    repNames.put(tPriority, repName);
                }
                repName.add(nameData);
            } else {
                Set<String> uniqueNames = new HashSet<String>();
                for (Map.Entry<Integer,List<NameData>> entry : repNames.entrySet()) {
                    for (NameData nData : entry.getValue()) {
                        String name = (nData.text == null) ? "" : nData.text.toLowerCase();
                        if (uniqueNames.contains(name)) {
                            count = omitByType.get(nData.typeId);
                            count = (count == null) ? 1 : count+1;
                            omitByType.put(nData.typeId, count);
                        } else {
                            uniqueNames.add(name);
                        }
                    }
                }
                currRepId = nameData.repId;
                repNames.clear();
            }
        }
        nameRS.close();

        int nameTot = 0;
        System.out.println("\nName count by type ...");
        for (Map.Entry<Integer,Integer> entry : countByType.entrySet()) {
            System.out.println(entry.getKey() + "|" + typeMap.get(entry.getKey()) +
                "|" + priorityMap.get(typeMap.get(entry.getKey())) + "|" + entry.getValue());
            nameTot += entry.getValue();
        }
        System.out.println("\nTotal: " + nameTot);

        int omitTot = 0;
        System.out.println("\nDuplicate name count by type ...");
        for (Map.Entry<Integer,Integer> entry : omitByType.entrySet()) {
            System.out.println(entry.getKey() + "|" + typeMap.get(entry.getKey()) +
                "|" + priorityMap.get(typeMap.get(entry.getKey())) + "|" + entry.getValue());
            omitTot += entry.getValue();
        }
        System.out.println("\nTotal: " + omitTot);
        System.exit(0);
    }
}
