package std.wlj.solr;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.dbdump.DumpTypes;
import std.wlj.util.SolrManager;

public class DumpDisplayNamesForTopTwoLevels {

    static final int MAX_ROWS = 2500;

    private static class RepDataTiny {
        int repId;
        int parentId;
        int typeId;
        boolean isCertified;
        boolean isAccepted;
        boolean isProvisional;
        Map<String, String> dispNames = new HashMap<>();
    }

    private static final List<String> finalResults = new ArrayList<>(64000);

    private static final Map<String, String> typeData = DumpTypes.loadPlaceTypes();

    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        List<RepDataTiny> level01Reps = repsFromQuery(solrConn, "parentId:[-1 TO -1] AND deleteId:0");
        Set<String> locales = level01Reps.stream()
                    .map(rr -> rr.dispNames.keySet())
                    .flatMap(ss -> ss.stream())
                    .collect(Collectors.toCollection(TreeSet::new));
        locales.add("tet");
        locales.add("zh-Latn");
        locales.add("ko-Latn");
        locales.add("tg");
        locales.add("liv");
        locales.add("ber");
        locales.add("krl");
        locales.add("ku");
        locales.add("sco");
        locales.add("enm");
        locales.add("non");
        locales.add("owl");
        locales.add("wlm");
        locales.add("ms-Arab");

        locales = setupLocalesToUse();

        finalResults.add(formatHeader(locales));

        for (RepDataTiny level01Rep : level01Reps) {
            if (level01Rep.repId > 0) {
                String query = "parentId: " + level01Rep.repId + " AND deleteId:0";
                List<RepDataTiny> level02Reps = repsFromQuery(solrConn, query);
                dump(level01Rep, level02Reps, locales);
            }
        }
        Files.write(Paths.get("C:/temp/rep-display-names.txt"), finalResults, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.exit(0);
    }

    static List<RepDataTiny> repsFromQuery(SolrConnection solrConn, String queryStr) {
        List<RepDataTiny> results = new ArrayList<>();

        SolrQuery query = new SolrQuery(queryStr);
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.asc);

        try {
            List<PlaceRepDoc> docs = solrConn.search(query);
            System.out.println("Query: " + query + " --> count: " + docs.size());
            for (PlaceRepDoc doc : docs) {
                RepDataTiny rdt = new RepDataTiny();

                rdt.repId = doc.getRepId();
                rdt.parentId = doc.getParentId();
                rdt.typeId = doc.getType();
                rdt.isCertified = doc.isCertified();
                rdt.isAccepted = doc.isConfirmed();
                rdt.isProvisional = doc.isProvisional();
                rdt.dispNames = mungeLocales(doc.getDisplayNameMap());

                results.add(rdt);
            }
        } catch (PlaceDataException e) {
            System.out.println("Quot? " + e.getMessage());
        }

        return results;
    }

    static Map<String, String> mungeLocales(Map<String, String> dispNames) {
        Map<String, String> newDispNames = new HashMap<>();

        for (Map.Entry<String, String> entry : dispNames.entrySet()) {
            String locale = entry.getKey();

            int ndx0 = locale.indexOf('-');
            if (ndx0 <= 0) {
                newDispNames.put(locale, entry.getValue());
            } else {
                int ndx1 = locale.indexOf('-', ndx0+1);
                if (ndx1 <= 0) {
                    newDispNames.put(locale, entry.getValue());
                } else {
                    locale = locale.substring(0, ndx1);
                    if (! newDispNames.containsKey(locale)) {
                        newDispNames.put(locale, entry.getValue());
                    }
                }
            }
        }

        return newDispNames;
    }

    static void dump(RepDataTiny level01Rep, List<RepDataTiny> level02Reps, Set<String> locales) {
        finalResults.add("");
        finalResults.add("");
        finalResults.add(formatRep(1, level01Rep, locales));
        level02Reps.forEach(rep -> finalResults.add(formatRep(2, rep, locales)));
    }

    static String formatHeader(Set<String> locales) {
        StringBuilder buff = new StringBuilder();
        buff.append("||||||");
        buff.append("|en");
        locales.stream()
            .filter(ll -> ! "en".equals(ll))
            .forEach(ll -> buff.append("|").append(ll));
        return buff.toString();
    }

    static String formatRep(int level, RepDataTiny rep, Set<String> locales) {
        StringBuilder buff = new StringBuilder();
 
        if (level == 1) {
            buff.append(rep.repId);
            buff.append("|").append(typeData.getOrDefault(String.valueOf(rep.typeId), String.valueOf(rep.typeId)));
            buff.append("|").append(lockType(rep));
            buff.append("|||");
        } else {
            buff.append("|||").append(rep.repId);
            buff.append("|").append(typeData.getOrDefault(String.valueOf(rep.typeId), String.valueOf(rep.typeId)));
            buff.append("|").append(lockType(rep));
        }
        buff.append("|").append(rep.dispNames.getOrDefault("en", ""));

        // Check for missing locales
        rep.dispNames.keySet().stream()
            .filter(ll -> ! locales.contains(ll))
            .forEach(ll -> System.out.println("Missing locale: '" + ll + "'"));

        for (String locale : locales) {
            if (! ("en".equals(locale))) {
                buff.append("|").append(rep.dispNames.getOrDefault(locale, ""));
            }
        }
        return buff.toString();
    }

    static String lockType(RepDataTiny rep) {
        if (rep.isCertified) {
            return "CERTIFIED";
        } else if (rep.isAccepted) {
            return "ACCEPTED";
        } else {
            return "PROVISIONAL";
        }
    }

    static Set<String> setupLocalesToUse() {
        Set<String> locales = new TreeSet<>();

        locales.add(StdLocale.ENGLISH.getLocaleAsString());
        locales.add(StdLocale.ALBANIAN.getLocaleAsString());
        locales.add(StdLocale.ARMENIAN.getLocaleAsString());
        locales.add(StdLocale.BELARUSIAN.getLocaleAsString());
        locales.add(StdLocale.BULGARIAN.getLocaleAsString());
        locales.add(StdLocale.CHINESE.getLocaleAsString());
        locales.add(StdLocale.CHINESE_SIMPLIFIED.getLocaleAsString());
        locales.add(StdLocale.CHINESE_TRADITIONAL.getLocaleAsString());
        locales.add(StdLocale.CROATIAN.getLocaleAsString());
        locales.add(StdLocale.CZECH.getLocaleAsString());
        locales.add(StdLocale.DANISH.getLocaleAsString());
        locales.add(StdLocale.DUTCH.getLocaleAsString());
        locales.add(StdLocale.ESTONIAN.getLocaleAsString());
        locales.add(StdLocale.FIJIAN.getLocaleAsString());
        locales.add(StdLocale.FINNISH.getLocaleAsString());
        locales.add(StdLocale.FRENCH.getLocaleAsString());
        locales.add(StdLocale.GERMAN.getLocaleAsString());
        locales.add(StdLocale.HAITIAN.getLocaleAsString());
        locales.add(StdLocale.HUNGARIAN.getLocaleAsString());
        locales.add(StdLocale.INDONESIAN.getLocaleAsString());
        locales.add(StdLocale.ITALIAN.getLocaleAsString());
        locales.add(StdLocale.JAPANESE.getLocaleAsString());
        locales.add(StdLocale.JAPANESE_KANA.getLocaleAsString());
        locales.add(StdLocale.JAPANESE_KANJI.getLocaleAsString());
        locales.add(StdLocale.KHMER.getLocaleAsString());
        locales.add(StdLocale.KOREAN.getLocaleAsString());
        locales.add(StdLocale.KOREAN_HANGUL.getLocaleAsString());
        locales.add(StdLocale.KOREAN_HANJA.getLocaleAsString());
        locales.add(StdLocale.LAO.getLocaleAsString());
        locales.add(StdLocale.LATVIAN.getLocaleAsString());
        locales.add(StdLocale.LITHUANIAN.getLocaleAsString());
        locales.add(StdLocale.MACEDONIAN.getLocaleAsString());
        locales.add(StdLocale.MALAY.getLocaleAsString());
        locales.add(StdLocale.MONGOLIAN.getLocaleAsString());
        locales.add(StdLocale.NEPALI.getLocaleAsString());
        locales.add(StdLocale.NORWEGIAN.getLocaleAsString());
        locales.add(StdLocale.POLISH.getLocaleAsString());
        locales.add(StdLocale.PORTUGUESE.getLocaleAsString());
        locales.add(StdLocale.ROMANIAN.getLocaleAsString());
        locales.add(StdLocale.RUSSIAN.getLocaleAsString());
        locales.add(StdLocale.SAMOAN.getLocaleAsString());
        locales.add(StdLocale.SERBIAN.getLocaleAsString());
        locales.add(StdLocale.SINHALA.getLocaleAsString());
        locales.add(StdLocale.SLOVAK.getLocaleAsString());
        locales.add(StdLocale.SLOVENIAN.getLocaleAsString());
        locales.add(StdLocale.SPANISH.getLocaleAsString());
        locales.add(StdLocale.SWEDISH.getLocaleAsString());
        locales.add(StdLocale.THAI.getLocaleAsString());
        locales.add(StdLocale.TONGAN.getLocaleAsString());
        locales.add(StdLocale.TURKISH.getLocaleAsString());
        locales.add(StdLocale.UKRAINIAN.getLocaleAsString());
        locales.add(StdLocale.VIETNAMESE.getLocaleAsString());
        locales.add(StdLocale.ARMENIAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.BELARUSIAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.BULGARIAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.CHINESE_LATIN.getLocaleAsString());
        locales.add(StdLocale.GREEK_LATIN.getLocaleAsString());
        locales.add(StdLocale.JAPANESE_LATIN.getLocaleAsString());
        locales.add(StdLocale.KOREAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.LAO_LATIN.getLocaleAsString());
        locales.add(StdLocale.MACEDONIAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.MONGOLIAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.NEPALI_LATIN.getLocaleAsString());
        locales.add(StdLocale.RUSSIAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.SERBIAN_LATIN.getLocaleAsString());
        locales.add(StdLocale.SINHALA_LATIN.getLocaleAsString());
        locales.add(StdLocale.THAI_LATIN.getLocaleAsString());
        locales.add(StdLocale.UKRAINIAN_LATIN.getLocaleAsString());

        return locales;
    }
}
