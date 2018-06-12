/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Run a bunch of dates against both "DEV" and "LOCAL", to ensure that the results are as same as
 * necessary.  The purpose is to ensure that nothing important was broken!!
 * 
 * @author wjohnson000
 *
 */
public class SanityCheck {

    private static class InterpResult {
        String  gedcomx;
        boolean isAssisted;
        boolean containsUgly;

        public String toString() {
            return gedcomx + " [assist=" + isAssisted + "]  [ugly=" + containsUgly + "]";
        }
    }

    private static final String DEV_BASE_URL   = "http://ws.date.std.cmn.dev.us-east-1.dev.fslocal.org/dates/interp?text=";
    private static final String LOCAL_BASE_URL = "http://localhost:8080/std-ws-date/dates/interp?text=";

    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(3);
        httpConnManager.setDefaultMaxPerRoute(3);
    }

    public static void main(String... args) throws IOException {
        String url;
        String rawJson;

        List<String> datesToParse = Files.readAllLines(Paths.get("C:/temp/zh-dates.txt"), Charset.forName("UTF-8"));
//        datesToParse.clear();
//        datesToParse.add("民國105年1月15日");
//        datesToParse.add("順帝丙寅七月七日");
//        datesToParse.add("順帝三年丙寅七月七日");
//        datesToParse.add("民國庚子年12月");
//        datesToParse.add("道武帝皇始三年七月七日");

        List<String> allV1    = new ArrayList<>(1000);
        List<String> allV2    = new ArrayList<>(1000);
        List<String> mismatch = new ArrayList<>(1000);
        List<String> allMatch = new ArrayList<>(1000);
        List<String> noInterp = new ArrayList<>(1000);

        for (String dateStr : datesToParse) {
            if (dateStr.trim().isEmpty()) {
                continue;
            }

            String encDate = dateStr;
            try {
                encDate = URLEncoder.encode(dateStr, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                System.out.println("Unable to encode: " + dateStr + " --> " + ex.getMessage());
            }

            url = DEV_BASE_URL + encDate + "&langHint=zh";
            rawJson = getRawResults(url, "application/json");
            List<InterpResult> beta = parseResults(rawJson);

            url = DEV_BASE_URL + encDate + "&langHint=zh&experiments=use.v2.cjk";
            rawJson = getRawResults(url, "application/json");
            List<InterpResult> betaX = parseResults(rawJson);

            url = LOCAL_BASE_URL + encDate + "&langHint=zh";
            rawJson = getRawResults(url, "application/json");
            List<InterpResult> local = parseResults(rawJson);

            url = LOCAL_BASE_URL + encDate + "&langHint=zh&experiments=use.v2.cjk";
            rawJson = getRawResults(url, "application/json");
            List<InterpResult> localX = parseResults(rawJson);

            if (noInterp(beta, betaX, local, localX)) {
                noInterp.add("");
                noInterp.add(dateStr);
            } else if (allAssisted(beta, betaX, local, localX)) {
                allV1.add("");
                allV1.add(dateStr);
                beta.forEach(res -> allV1.add("  ALL: " + res.toString()));
            } else if (noneAssisted(beta, betaX, local, localX)) {
                allV2.add("");
                allV2.add(dateStr);
                beta.forEach(res -> allV2.add("  ALL: " + res.toString()));
            } else if (v2MatchV1(beta, betaX, local, localX)) {
                allMatch.add("");
                allMatch.add(dateStr);
                beta.forEach(res -> allMatch.add("  V1B: " + res.toString()));
                localX.forEach(res -> allMatch.add("  LLX: " + res.toString()));
            } else {
                mismatch.add("");
                mismatch.add(dateStr);
                beta.forEach(res -> mismatch.add("  BBB: " + res.toString()));
                betaX.forEach(res -> mismatch.add("  BBX: " + res.toString()));
                local.forEach(res -> mismatch.add("  LLL: " + res.toString()));
                localX.forEach(res -> mismatch.add("  LLX: " + res.toString()));
            }
        }

        System.out.println("\n\n==============================================================================");
        System.out.println("All V1");
        System.out.println("==============================================================================");
        allV1.forEach(System.out::println);

        System.out.println("\n\n==============================================================================");
        System.out.println("All V2");
        System.out.println("==============================================================================");
        allV2.forEach(System.out::println);
        
        System.out.println("\n\n==============================================================================");
        System.out.println("New V2, matches V1");
        System.out.println("==============================================================================");
        allMatch.forEach(System.out::println);
        
        System.out.println("\n\n==============================================================================");
        System.out.println("New V2, but doesn't match V1");
        System.out.println("==============================================================================");
        mismatch.forEach(System.out::println);

        System.out.println("\n\n==============================================================================");
        System.out.println("No Interpretation");
        System.out.println("==============================================================================");
        noInterp.forEach(System.out::println);
    }

    static boolean noInterp(List<InterpResult> beta, List<InterpResult> betaX, List<InterpResult> local, List<InterpResult> localX) {
        if (beta.isEmpty()  &&  betaX.isEmpty()  &&  local.isEmpty()  &&  localX.isEmpty()) {
            return true;
        }
        return false;
    }

    static boolean allAssisted(List<InterpResult> beta, List<InterpResult> betaX, List<InterpResult> local, List<InterpResult> localX) {
        if (beta.size() > 0  &&  beta.get(0).isAssisted  &&  betaX.get(0).isAssisted  &&  local.get(0).isAssisted  &&  localX.get(0).isAssisted) {
            return true;
        }
        return false;
    }

    static boolean noneAssisted(List<InterpResult> beta, List<InterpResult> betaX, List<InterpResult> local, List<InterpResult> localX) {
        if (beta.size() > 0  &&  !beta.get(0).isAssisted  &&  !betaX.get(0).isAssisted  &&  !local.get(0).isAssisted  &&  !localX.get(0).isAssisted) {
            return true;
        }
        return false;
    }

    static boolean v2MatchV1(List<InterpResult> beta, List<InterpResult> betaX, List<InterpResult> local, List<InterpResult> localX) {
        if (beta.size() > 0  &&  localX.size() > 0  &&  beta.get(0).isAssisted  &&  ! localX.get(0).isAssisted  && beta.get(0).gedcomx.equals(localX.get(0).gedcomx)) {
            return true;
        }
        return false;
    }

    static String getRawResults(String url, String accept) {
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", accept);

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            String results = IOUtils.toString(ios, Charset.forName("UTF-8"));
            EntityUtils.consumeQuietly(response.getEntity());
            return results;
        } catch (Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
            return null;
        }
    }

    static List<InterpResult> parseResults(String rawJson) {
        List<InterpResult> results = new ArrayList<>();
        JSONObject json = new JSONObject(rawJson);
        JSONArray dates = json.getJSONArray("dates");
        for (int ndx=0;  ndx<dates.length();  ndx++) {
            JSONObject date   = dates.getJSONObject(ndx);
            JSONObject interp = date.getJSONObject("interp");

            InterpResult result = new InterpResult();
            result.gedcomx = date.getString("gedcomx");
            if (interp != null) {
                result.isAssisted = interp.getBoolean("assisted");
                result.containsUgly = interp.getBoolean("containsNonDateText");
            }
            results.add(result);
        }

        return results;
    }
}
