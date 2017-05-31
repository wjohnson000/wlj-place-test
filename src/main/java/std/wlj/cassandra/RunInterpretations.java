package std.wlj.cassandra;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.familysearch.standards.place.ws.model.AnnotationModel;
import org.familysearch.standards.place.ws.model.InterpretationModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultModel;
import org.familysearch.standards.place.ws.model.RootModel;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

import std.wlj.ws.rawhttp.HttpHelper;

public class RunInterpretations {

    /** Base URL of the application */
    private static String baseUrl = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";

    private static ExecutorService execService = Executors.newFixedThreadPool(20);

    private static Random random = new Random();
    private static Charset UTF_8 = Charset.forName("UTF-8");
    private static List<String> interpName;

    private static long interpCnt = 0;
    private static long interpTime = 0;

    private static Session session;
    private static PreparedStatement pstmt;

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        interpName = getSearchValues("C:/temp/places-interp-name.txt");

        HttpHelper.overrideHTTPS = true;
        HttpHelper.doVerbose = false;
        HttpHelper.acceptType = "application/standards-places-v2+xml";

        long sss = System.nanoTime();
        setupCassandra();
        startInterp(8, 15000, session);

        execService.shutdown();
        execService.awaitTermination(60, TimeUnit.MINUTES);
        long eee = System.nanoTime();
        session.close();

        System.out.println("\n=======================================================================================");
        System.out.println("Time: " + (eee-sss) / 1_000_000.0);
        System.out.println("Interp : " + interpCnt + " --> " + (interpTime/1_000_000.0));
        System.exit(0);
    }

    static void setupCassandra() {
        session = DataStaxUtil.connect();

        new MappingManager(session).udtCodec(InterpResult.class);

        String uglyQuery =
                "INSERT INTO Interpretation " +
                "(id, t_text, params, t_parse, last_updated, results, rep_ids, raw_scores, rel_scores) " + 
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        pstmt = session.prepare(uglyQuery);
    }

    static void startInterp(int thrCount, int times, Session session) {
        for (int i=0;  i<thrCount;  i++) {
            execService.submit(
                () -> {
                    for (int cnt=0;  cnt<times;  cnt++) {
                        try {
                            int ndx = random.nextInt(interpName.size());
                            doInterp(interpName.get(ndx), session);
                            Thread.sleep(8L);
                        } catch (Exception e) { }
                    }
                });
        }
    }

    static void doInterp(String text, Session session) throws Exception {
        interpCnt++;

        URL url = new URL(baseUrl + "/interp");
        long nnn01 = System.nanoTime();
        RootModel root = HttpHelper.doGET(url, "name", text, "metrics", "true");
        long nnn02 = System.nanoTime();
        saveInterp(text, root, session);
        long nnn03 = System.nanoTime();
        System.out.println("GET time: " + (nnn02-nnn01)/1_000_000.0);
        System.out.println("CSS time: " + (nnn03-nnn02)/1_000_000.0);
    }

    static void saveInterp(String text, RootModel root, Session session) {
        Map<String,String> interpParams = new HashMap<>();

        List<String>       annotations   = new ArrayList<>();
        List<InterpResult> interpResults = new ArrayList<>();
        List<Integer>      repIds        = new ArrayList<>();
        List<Integer>      rawScores     = new ArrayList<>();
        List<Integer>      relScores     = new ArrayList<>();

        InterpretationModel interpModel  = root.getInterp();
        if (interpModel.getAnnotations() != null) {
            for (AnnotationModel annModel : interpModel.getAnnotations()) {
                annotations.add(annModel.getName() + "=" + annModel.getCode());
            }
        }

        if (interpModel.getReps() != null) {
            for (PlaceSearchResultModel reqModel : interpModel.getReps()) {
                int repId = reqModel.getRep().getId();
                int rawScore = reqModel.getRawScore();
                int relScore = reqModel.getRelevanceScore();
                repIds.add(repId);
                rawScores.add(rawScore);
                relScores.add(relScore);
                interpResults.add(new InterpResult(repId, rawScore, relScore));
            }
        }

        if (interpModel.getAltReps() != null) {
            for (PlaceSearchResultModel reqModel : interpModel.getAltReps()) {
                int repId = reqModel.getRep().getId();
                int rawScore = reqModel.getRawScore();
                int relScore = reqModel.getRelevanceScore();
                repIds.add(repId);
                rawScores.add(rawScore);
                relScores.add(relScore);
                interpResults.add(new InterpResult(repId, rawScore, relScore));
            }
        }

        BoundStatement bstmt = pstmt.bind(
                UUID.randomUUID(),
                text,
                interpParams,
                annotations,
                new java.util.Date(),
                interpResults,
                repIds,
                rawScores,
                relScores);
        session.execute(bstmt);
    }

    static List<String> getSearchValues(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), UTF_8)
                .stream()
                .map(val -> val.replace('"', ' ').trim())
                .filter(val -> val.length() > 4)
                .collect(Collectors.toList());
    }
}
