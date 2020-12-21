/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class GenAttrUrlTitleThreaded {

    static class TitleTrain {
        public String url;
        public String title = null;
        public Thread thread = null;
        public boolean done = false;

        public TitleTrain(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public boolean isDone() {
            return done;
        }

        public void startThread() {
            Runnable runx = () -> {
                TitleSaxHandler titleHandler = new TitleSaxHandler();
                title = titleHandler.parseTitle(url);
                done = true;
            };
            thread = new Thread(runx);
            thread.start();
        }
    }

    private static final String ATTR_FILE  = "C:/temp/db-dump/attribute-all.txt";
    private static final String TITLE_FILE = "C:/temp/attr-url-title-easy.txt";

    private static final Set<String> badDomains = new HashSet<>();
    static {
        badDomains.add("constituteproject.org");
        badDomains.add("ucblibraries.colorado.edu");
        badDomains.add("google");
        badDomains.add("easyweb.easynet.co");
        badDomains.add("lcweb2.loc.gov");
        badDomains.add("www.cia.gov");
        badDomains.add("cafe.naver");
        badDomains.add("vlib.iue");
        badDomains.add("ericdigests");
        badDomains.add("entertainment.timesonline");
        badDomains.add(".edu/");
        badDomains.add("manioc.org");
        badDomains.add("dreptonline");
        badDomains.add("obs.coe.int");
        badDomains.add("kryeministria");
        badDomains.add("dtic");
        badDomains.add("britannica");
        badDomains.add("webcitation");
        badDomains.add("wildmadagascar");
        badDomains.add("unicef");
        badDomains.add("jps.auckland");
        badDomains.add("srbija");
        badDomains.add("business-anti-corruption");
        badDomains.add("oapen");
        badDomains.add("download");
        badDomains.add("moi.gov");
        badDomains.add("theguardian");
        badDomains.add("www.niue");
        badDomains.add("wits.worldbank");
        badDomains.add("oclc.org");
        badDomains.add("publications.parliament");
        badDomains.add("banglapedia");
        badDomains.add("ccel.org");
        badDomains.add(".ine.gov.");
        badDomains.add("pakistan.gov");
        badDomains.add(".go.id");
        badDomains.add("worldbank.org");
        badDomains.add("archive.org");
        badDomains.add("un.org");
        badDomains.add("palauconsulate.be");
        badDomains.add("washingtonpost.com");
        badDomains.add("bqdoha.com");
        badDomains.add("globalintegrity.org");
        badDomains.add("cambodia.gov");
        badDomains.add("uzonreport.com");
        badDomains.add("photostaud.com");
        badDomains.add("andorramania.com");
        badDomains.add("botswanagolf");
        badDomains.add("rfi.fr");
        badDomains.add("viewfinder");
    }

    private static final Set<String> badExtensions = new HashSet<>();
    static {
        badExtensions.add(".stm");
        badExtensions.add(".pdf");
        badExtensions.add(".php");
        badExtensions.add(".do");
        badExtensions.add(".jpg");
        badExtensions.add(".jpg");
        badExtensions.add(".ro/");
    }

    private static List<TitleTrain> runningTrains = new ArrayList<>();

    public static void main(String... args) throws IOException {
        Map<String,String> urlTitle = loadTitles();

        boolean changed = false;
        int tcount = 0;
        try (FileResultSet attrRS = new FileResultSet()) {
            attrRS.setSeparator("\\|");
            attrRS.openFile(ATTR_FILE);
            while (attrRS.next()) {
                if (++tcount % 1000 == 0) System.out.println("Rows=" + tcount);
                String value = attrRS.getString("attr_value");

                if (tcount > 132000  &&  value != null  &&  value.startsWith("http")  &&  ! urlTitle.containsKey(value)  &&  checkTitle(value)) {
                    TitleTrain aTrain = new TitleTrain(value);
                    runningTrains.add(aTrain);
                    aTrain.startThread();
                    while (runningTrains.size() > 48) {
                        TitleTrain doneTrain = runningTrains.stream().filter(train -> train.isDone()).findFirst().orElse(null);
                        if (doneTrain == null) {
                            try { Thread.sleep(100L); } catch(Exception ex) { }
                        } else {
                            runningTrains.remove(doneTrain);
                            String title = doneTrain.getTitle();
                            if (title != null  &&  title.trim().length() > 4  &&  ! title.contains("301 Moved")) {
                                title = title.replace('\r', ' ').replace('\n', ' ').trim();
                                System.out.println(doneTrain.getUrl() + " --> " + title);
                                changed = true;
                                urlTitle.put(doneTrain.getUrl(), title);
                            }
                        }
                    }
                }

                if (changed  &&  urlTitle.size() % 100 == 0) {
                    Files.write(Paths.get(TITLE_FILE), formatTitles(urlTitle), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                }
            }
        } catch (Exception ex) {
            System.out.println("OOPS: " + ex.getMessage());
        }

        Files.write(Paths.get(TITLE_FILE), formatTitles(urlTitle), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    protected static boolean checkTitle(final String url) {
        boolean goodUrl       = badDomains.stream().noneMatch(domain -> url.contains(domain));
        boolean goodExtension = badExtensions.stream().noneMatch(ext -> url.contains(ext));
        return (goodUrl  &&  goodExtension);
    }

    protected static Map<String, String> loadTitles() {
        List<String> titleData;
        try {
            titleData = Files.readAllLines(Paths.get(TITLE_FILE), StandardCharsets.UTF_8);
            return titleData.stream()
                    .map(line -> PlaceHelper.split(line, '|'))
                    .filter(arr -> arr.length > 1)
                    .collect(Collectors.toMap(
                            arr -> arr[0],
                            arr -> arr[1],
                            (val1, val2) -> val1,
                            TreeMap::new));
        } catch (IOException e) {
            return new TreeMap<>();
        }
    }

    protected static List<String> formatTitles(Map<String, String> urlData) {
        return urlData.entrySet().stream()
            .map(entry -> entry.getKey() + "|" + entry.getValue())
            .collect(Collectors.toList());
    }
}
