/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.databind.JsonNode;

import std.wlj.json.JsonUtility;


/**
 * WICF (Where [did] I Come From) facts from the "Discovery" team.  De-normalize the data, which was
 * provided in CSV format.  NOTE: some rows span multiple lines!!  NOTE: the "apache-commons" CSV
 * package is used to parse the data, which handles the optional double quotes, multiple-line span,
 * etc.  Yeah!!
 *  
 * @author wjohnson000
 *
 */
public class FactsWICF {

    static class CountryInfo {
        int    wicfId;
        String wicfCode;
        int    repId;
        Map<String, String> names = new TreeMap<>();
    }

    private static final String BASE_DIR  = "C:/D-drive/homelands/WICF/csv";
    private static final TextCleaner cleaner = new TextCleaner();
    
    /** Map containing the translation-key --> Map of language --> value */
    private static final Map<Integer, Map<String, String>> trxValues = new TreeMap<>();

    private static final Map<Integer, CountryInfo> countryData = new TreeMap<>();

    private static final Map<String, Integer> columnCount = new TreeMap<>();

    public static void main(String...args) throws Exception {
        loadTranslationData();
        loadCountries();
        for (CountryInfo ci : countryData.values()) {
            System.out.println("CI: " + ci.wicfId + " | " + ci.wicfCode + " | " + ci.repId + " | " + ci.names);
        }

        try (Stream<Path> files = Files.list(Paths.get(BASE_DIR))) {
            files.forEach(pp -> {
                processDataFile(pp);
            });
        }

        System.out.println("\n");
        columnCount.entrySet().forEach(System.out::println);
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
                CountryInfo ci = makeCountry(row);
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
        return readAll(Paths.get(BASE_DIR, filename));
    }

    /**
     * Read all "records" from a file given the name.  Each logical row -- which may span multiple
     * lines -- will be split into individual fields at the comma and optional double-quotes.
     * 
     * @param path path to the file
     * @return List of logical rows, split into individual fields
     */
    static List<List<String>> readAll(Path path) throws IOException {
        List<List<String>> rowData = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
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

    static void processDataFile(Path pp) {
        if (pp.getFileName().toString().endsWith(".txt")  ||
                pp.getFileName().toString().startsWith(".")  ||
                pp.getFileName().toString().startsWith("0")  ||
                pp.getFileName().toString().startsWith("1-")) {
          return;
        }

        String type = pp.getFileName().toString();
        int ndx = type.indexOf('-');
        type = type.substring(ndx+1);
        type = type.replace("-data.csv", "");

        System.out.println("\nFF: " + pp + " --> " + pp.getFileName());
        System.out.println("   Type  : " + type);
        String outFileName = pp.getFileName().toString().replace(".csv", ".txt");

        List<String> contents = new ArrayList<>(1_000);
        try {
            List<List<String>> rowData = readAll(pp);
            List<String> header = getHeaders(rowData.get(0));
            System.out.println("   Header: " + header);
            System.out.println("   Rows  : " + rowData.size());

            for (String hdr : header) {
                Integer count = columnCount.getOrDefault(hdr, 0);
                columnCount.put(hdr, count+1);
            }

            boolean first = true;
            for (List<String> row : rowData) {
                if (first) {
                    first = false;
                } else if (row.size() != header.size()) {
                    System.out.println("   Row doesn't match headers:" + row);
                } else {
                    JsonNode json = JsonUtility.parseJson("{}");
                    JsonUtility.addField(json, "type", type);
                    
                    for (int col=0;  col<header.size();  col++) {
                        if (header.get(col).equals("country_id")) {
                            try {
                                int extid = Integer.parseInt(row.get(col));
                                CountryInfo ci = countryData.get(extid);
                                if (ci == null) {
                                    System.out.println("   Invalid country ID: " + row.get(col));
                                } else {
                                    JsonUtility.addField(json, "countryId", ci.wicfId);
                                    JsonUtility.addField(json, "countryCode", ci.wicfCode);
                                    JsonUtility.addField(json, "repid", ci.repId);
                                }
                            } catch(NumberFormatException ex) {
                                System.out.println("   Invalid country ID: " + row.get(col));
                            }
                        } else if (header.get(col).equals("year")) {
                            try {
                                int year = Integer.parseInt(row.get(col));
                                JsonUtility.addField(json, "year", year);
                            } catch(NumberFormatException ex) {
                                System.out.println("   Invalid year: " + row.get(col));
                            }
                        } else {
                            try {
                                int extid = Integer.parseInt(row.get(col));
                                Map<String, String> values = trxValues.get(extid);
                                for (Map.Entry<String, String> entry : values.entrySet()) {
                                    JsonUtility.addField(json, header.get(col) + "." + entry.getKey(), cleaner.cleanse(entry.getValue()));
                                }
                            } catch(NumberFormatException ex) {
                                if (! row.get(col).trim().isEmpty()) {
                                    JsonUtility.addArray(json, header.get(col),  row.get(col)); 
                                }
                            }
                        }
                    }

                    contents.add(json.toString());
                }
            }

            Files.write(Paths.get(BASE_DIR, outFileName), contents, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch(Exception ex) {
            System.out.println("Unable to process file: " + pp + " --> " + ex.getMessage());
        }
    }

    static CountryInfo makeCountry(List<String> rawData) {
        Map<Integer, Integer> id2IdMap = MapWicfToRep.mapWicfToPlace();

        CountryInfo ci = new CountryInfo();

        ci.wicfId = Integer.parseInt(rawData.get(0));
        ci.wicfCode = rawData.get(1);
        ci.names = trxValues.get(ci.wicfId);
        ci.repId = id2IdMap.getOrDefault(ci.wicfId, 0);

        return ci;
    }

    static List<String> getHeaders(List<String> data) {
        return data.stream()
            .map(tx -> tx.replace("_translation_key_id", ""))
            .collect(Collectors.toList());
    }
}
