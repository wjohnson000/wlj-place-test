package std.wlj.db2solr;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.familysearch.standards.place.data.solr.PlaceRepDoc;

import std.wlj.util.FileUtils;

import com.google.gson.Gson;


public class Step07CompareFiles {
    public static void main(String... args) {
        Map<String,PlaceRepDoc> afterMap = parseDocs("C:/temp/load-place-db/solr-content-04-after.txt");
        Map<String,PlaceRepDoc> finalMap = parseDocs("C:/temp/load-place-db/solr-content-06-final.txt");
        System.out.println("After count: " + afterMap.size());
        System.out.println("Final count: " + finalMap.size());

        // Compare the documents, one-by-one, and show any differences ...
        Set<String> docIds = new TreeSet<String>();
        docIds.addAll(afterMap.keySet());
        docIds.addAll(finalMap.keySet());
        for (String docId : docIds) {
            PlaceRepDoc doc01 = afterMap.get(docId);
            PlaceRepDoc doc02 = finalMap.get(docId);
            if (doc01 == null) {
                System.out.println(docId + " --> Only in FINAL\n");
            } else if (doc02 == null) {
                System.out.println(docId + " --> Only in AFTER\n");
            } else {
                compareDocs(doc01, doc02);
            }
        }
    }

    /**
     * Parse a file containing a bunch of place-rep docs that have been JSON-ized, and
     * transmogrify them back into place-rep docs ...
     * 
     * @param filePath path the file containing the JSON
     * @return list of place-rep docs
     */
    private static Map<String,PlaceRepDoc> parseDocs(String filePath) {
        Map<String,PlaceRepDoc> results = new TreeMap<>();
        Gson gson = new Gson();

        try (BufferedReader reader = FileUtils.getReader(filePath)) {
            String line = null;
            StringBuilder buff = new StringBuilder(4028);
            while((line = reader.readLine()) != null) {
                buff.append(line).append(" ");
                if (line.equals("}")) {
                    PlaceRepDoc doc = gson.fromJson(buff.toString(), PlaceRepDoc.class);
                    results.put(doc.getId(), doc);
                    buff = new StringBuilder(4028);
                }
            }
        } catch (Exception ex) {
            System.out.println("Unable to open or processfile: " + filePath);
        }

        return results;
    }

    /**
     * Compare two "place-rep doc" instances.
     * 
     * @param doc01 first place-rep doc
     * @param doc02 second place-rep doc
     */
    private static void compareDocs(PlaceRepDoc doc01, PlaceRepDoc doc02) {
        StringBuilder buff = new StringBuilder(512);

        compareValue(buff, "id", doc01.getId(), doc02.getId());
        compareValue(buff, "owner-id", doc01.getOwnerId(), doc02.getOwnerId());
        compareValue(buff, "rep-id", doc01.getRepId(), doc02.getRepId());
        compareValue(buff, "rep-chain-id", doc01.getRepIdChainAsInt(), doc02.getRepIdChainAsInt());
        compareValue(buff, "parent-id", doc01.getParentId(), doc02.getParentId());
        compareValue(buff, "start-year", doc01.getStartYear(), doc02.getStartYear());
        compareValue(buff, "end-year", doc01.getEndYear(), doc02.getEndYear());
        compareValue(buff, "owner-start-year", doc01.getOwnerStartYear(), doc02.getOwnerStartYear());
        compareValue(buff, "owner-end-year", doc01.getOwnerEndYear(), doc02.getOwnerEndYear());
        compareValue(buff, "latitude", doc01.getLatitude(), doc02.getLatitude());
        compareValue(buff, "longitude", doc01.getLongitude(), doc02.getLongitude());
        compareValue(buff, "pref-locale", doc01.getPrefLocale(), doc02.getPrefLocale());
        compareValue(buff, "owner-id", doc01.getType(), doc02.getType());
        compareValue(buff, "uuid", doc01.getUUID(), doc02.getUUID());
        compareValue(buff, "revision", doc01.getRevision(), doc02.getRevision());
        compareValue(buff, "fwd-revision", doc01.getForwardRevision(), doc02.getForwardRevision());
        compareValue(buff, "published", doc01.getPublished(), doc02.getPublished());
        compareValue(buff, "validated", doc01.getValidated(), doc02.getValidated());
        compareValue(buff, "type-group", doc01.getTypeGroup(), doc02.getTypeGroup());
        compareValue(buff, "delete-id", doc01.getDeleteId(), doc02.getDeleteId());
        compareValue(buff, "place-delete-id", doc01.getPlaceDeleteId(), doc02.getPlaceDeleteId());
        compareValue(buff, "display-names", doc01.getDisplayNames(), doc02.getDisplayNames());
        compareValue(buff, "variant-names", doc01.getVariantNames(), doc02.getVariantNames());
        compareValue(buff, "attributes", doc01.getAttributes(), doc02.getAttributes());
        compareValue(buff, "citations", doc01.getCitations(), doc02.getCitations());
        compareValue(buff, "ext-xrefs", doc01.getExtXrefs(), doc02.getExtXrefs());

        if (buff.length() == 0) {
            System.out.println("" + doc01.getId() + " --> SAME!!\n");
        } else {
            System.out.println("" + doc01.getId() + " --> DIFFERENT\n" + buff.toString());
        }
    }

    /**
     * Compare two "int" values
     * 
     * @param buff buffer where mismatches are written
     * @param field field name
     * @param val01 value one
     * @param val02 value 02
     */
    private static void compareValue(StringBuilder buff, String field, int val01, int val02) {
        if (val01 != val02) {
            buff.append("   ").append(field).append(": ").append(val01).append(" , ").append(val02).append("\n");
        }
    }

    /**
     * Compare two "Integer" values
     * 
     * @param buff buffer where mismatches are written
     * @param field field name
     * @param val01 value one
     * @param val02 value 02
     */
    private static void compareValue(StringBuilder buff, String field, Integer val01, Integer val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else {
            int int01 = (val01 == null) ? Integer.MAX_VALUE : val01.intValue();
            int int02 = (val02 == null) ? Integer.MAX_VALUE : val02.intValue();
            if (int01 != int02) {
                buff.append("   ").append(field).append(": ").append(val01).append(" , ").append(val02).append("\n");
            }
        }
    }

    /**
     * Compare two "Double" values
     * 
     * @param buff buffer where mismatches are written
     * @param field field name
     * @param val01 value one
     * @param val02 value 02
     */
    private static void compareValue(StringBuilder buff, String field, Double val01, Double val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null  ||  Math.abs(val01.doubleValue() - val02.doubleValue()) > 0.0001) {
            buff.append("   ").append(field).append(": ").append(val01).append(" , ").append(val02).append("\n");
        }
    }

    /**
     * Compare two "String" values
     * 
     * @param buff buffer where mismatches are written
     * @param field field name
     * @param val01 value one
     * @param val02 value 02
     */
    private static void compareValue(StringBuilder buff, String field, String val01, String val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null  ||  ! val01.equals(val02)) {
            buff.append("   ").append(field).append(": ").append(val01).append(" , ").append(val02).append("\n");
        }
    }

    /**
     * Compare two "int"-array values
     * 
     * @param buff buffer where mismatches are written
     * @param field field name
     * @param val01 value one
     * @param val02 value 02
     */
    private static void compareValue(StringBuilder buff, String field, int[] val01, int[] val02) {
        if (val01 == null  &&  val02 == null) {
            ;  // do nothing
        } else if (val01 == null  ||  val02 == null) {
            buff.append("   ").append(field).append(": ").append(val01).append(" , ").append(val02).append("\n");
        } else {
            String str01 = Arrays.toString(val01);
            String str02 = Arrays.toString(val02);
            if (! str01.equals(str02)) {
                buff.append("   ").append(field).append(": ").append(str01).append(" , ").append(str02).append("\n");
            }
        }
    }

    /**
     * Compare two "String"-list values
     * 
     * @param buff buffer where mismatches are written
     * @param field field name
     * @param val01 value one
     * @param val02 value 02
     */
    private static void compareValue(StringBuilder buff, String field, List<String> val01, List<String> val02) {
        Map<String,String> sMap01 = new TreeMap<>();
        Map<String,String> sMap02 = new TreeMap<>();
        Map<Integer,String> iMap01 = new TreeMap<>();
        Map<Integer,String> iMap02 = new TreeMap<>();

        if (val01 != null) {
            for (String v01 : val01) {
                int ndx = v01.indexOf('|');
                if (ndx == -1) {
                    sMap01.put(v01, v01);
                } else {
                   int iKey = -1;
                   String sKey = v01.substring(0, ndx);
                   sMap01.put(sKey, v01);

                   try { iKey = Integer.parseInt(sKey); } catch(Exception ex) { iKey = -1; }
                   if (iKey >= 0) {
                       iMap01.put(iKey, v01);
                   }
                }
            }
        }

        if (val02 != null) {
            for (String v02 : val02) {
                int ndx = v02.indexOf('|');
                if (ndx == -1) {
                    sMap02.put(v02, v02);
                } else {
                    int iKey = -1;
                    String sKey = v02.substring(0, ndx);
                    sMap02.put(sKey, v02);

                    try { iKey = Integer.parseInt(sKey); } catch(Exception ex) { iKey = -1; }
                    if (iKey >= 0) {
                        iMap02.put(iKey, v02);
                    }
                }
            }
        }

        if (iMap01.size() > 0  ||  iMap02.size() > 0) {
            Set<Integer> allKeys = new TreeSet<>();
            allKeys.addAll(iMap01.keySet());
            allKeys.addAll(iMap02.keySet());
            for (Integer key : allKeys) {
                String str01 = iMap01.get(key);
                String str02 = iMap02.get(key);
                if (str01 == null  ||  str02 == null  ||  ! str01.equals(str02)) {
                    buff.append("   ").append(field).append(": ").append(str01).append(" , ").append(str02).append("\n");
                }
            }
        } else {
            Set<String> allKeys = new TreeSet<>();
            allKeys.addAll(sMap01.keySet());
            allKeys.addAll(sMap02.keySet());
            for (String key : allKeys) {
                String str01 = sMap01.get(key);
                String str02 = sMap02.get(key);
                if (str01 == null  ||  str02 == null  ||  ! str01.equals(str02)) {
                    buff.append("   ").append(field).append(": ").append(str01).append(" , ").append(str02).append("\n");
                }
            }
        }
    }
}