/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.v1.DateV1Shim;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author wjohnson000
 *
 */
public class TestDateWeb {

//    private static final String BASE_URL = "www.familysearch.org/int-std-ws-date/dates/interp?text=&langHint=zh&experiments=use.v2.cjk";
    private static final String BASE_URL = "http://ws.date.std.cmn.beta.us-east-1.test.fslocal.org/dates/interp?text=";

    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(3);
        httpConnManager.setDefaultMaxPerRoute(3);
    }

    private static final List<String> v1Only     = new ArrayList<>(1000);
    private static final List<String> matches    = new ArrayList<>(1000);
    private static final List<String> mismatches = new ArrayList<>(1000);
    private static final List<String> noInterp   = new ArrayList<>(1000);

    public static void main(String...arg) throws Exception {
        String message;
        List<GenDateInterpResult> parseResult;
        GenDateInterpResult resultV1Shim;

        List<String> datesToParse = Files.readAllLines(Paths.get("C:/temp/zh-dates.txt"), Charset.forName("UTF-8"));
//        datesToParse.clear();
//        datesToParse.add("民國105年1月15日");

        for (String dateToParse : datesToParse) {
            resultV1Shim = null;
            try {
                parseResult = DateV1Shim.interpDate(dateToParse);
                resultV1Shim = parseResult.get(0);
                message = "";
            } catch(Exception ex) {
                message = ex.getMessage();
            }

            String encDate = dateToParse;
            try {
                encDate = URLEncoder.encode(dateToParse, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                System.out.println("Unable to encode: " + dateToParse + " --> " + ex.getMessage());
            }

            StringBuilder buff = new StringBuilder();
            buff.append(BASE_URL);
            buff.append(encDate);
            buff.append("&langHint=zh");

            String url01 = buff.toString();
            String rawJsonNoneExp = getRawResults(url01, "application/json");
            List<String[]> parseNoneExp = parseResults(rawJsonNoneExp);

            buff.append("&experiments=use.v2.cjk");
            String url02 = buff.toString();
            String rawJsonWithExp = getRawResults(url02, "application/json");
            List<String[]> parseWithExp = parseResults(rawJsonWithExp);

            String v1Date = (resultV1Shim == null) ? "" : resultV1Shim.getDate().toGEDCOMX();
            if (parseNoneExp.isEmpty()  &&  parseWithExp.isEmpty()) {
                noInterp.add(dateToParse + "|" + message);
            } else if (isV1Interp(parseNoneExp)  &&  isV1Interp(parseWithExp)) {
                String[] data = parseNoneExp.get(0);
                v1Only.add(dateToParse + "|" + v1Date + "|" + data[0] + "|" + data[1] + "|" + data[3]);
            } else if (isV1Match(parseNoneExp, v1Date)  &&  ! isV1Interp(parseNoneExp)) {
                String[] data = parseNoneExp.get(0);
                matches.add(dateToParse + "|" + v1Date + "|" + data[0] + "|" + data[1] + "|" + data[3]);
            } else if (isV1Match(parseWithExp, v1Date)) {
                String[] data = parseWithExp.get(0);
                matches.add(dateToParse + "|" + v1Date + "|" + data[0] + "|" + data[1] + "|" + data[3] + "|experiment");
            } else {
                String[] data = parseWithExp.get(0);
                mismatches.add(dateToParse + "|" + v1Date + "|" + data[0] + "|" + data[1] + "|" + data[3] + "|experiment");
            }
        }

        System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Orig. Text|V1 Result|V2 Result|Is Approx?|Has Non-Date Text?");
        matches.forEach(System.out::println);

        System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Orig. Text|V1 Result|V2 Result|Is Approx?|Has Non-Date Text?");
        mismatches.forEach(System.out::println);

        System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Orig. Text|V1 Result|V1 Result|Is Approx?|Has Non-Date Text?");
        v1Only.forEach(System.out::println);

        System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Orig. Text|Message");
        noInterp.forEach(System.out::println);
    }

    static boolean isV1Interp(List<String[]> data) {
        if (data.isEmpty()) {
            return false;
        } else {
            return "true".equals(data.get(0)[2]);
        }
    }
    
    static boolean isV1Match(List<String[]> data, String v1Date) {
        if (data.isEmpty()  ||  v1Date.isEmpty()) {
            return false;
        } else {
            return v1Date.equals(data.get(0)[0]);
        }
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

    static List<String[]> parseResults(String rawJson) {
        List<String[]> results = new ArrayList<>();

        JSONObject json = new JSONObject(rawJson);
        JSONArray dates = json.getJSONArray("dates");
        for (int ndx=0;  ndx<dates.length();  ndx++) {
            String[] result = new String[4];
            JSONObject date   = dates.getJSONObject(ndx);
            JSONObject interp = date.getJSONObject("interp");
            result[0] = date.getString("gedcomx");
            result[1] = String.valueOf(date.getBoolean("isApproximate"));
            if (interp != null) {
                result[2] = String.valueOf(interp.getBoolean("assisted"));
                result[3] = String.valueOf(interp.getBoolean("containsNonDateText"));
            }
            results.add(result);
        }

        return results;
    }

    static List<String> dumpStuff(String origDateString, String v1Result, String message, List<String[]> interp01, List<String[]> interp02) {
        List<String> lines = new ArrayList<>(5);

        lines.add("");
        if (interp02.isEmpty()) {
            lines.add(origDateString + "|" + v1Result + "|" + message);
        } else {
            lines.add(origDateString + "|" + v1Result);
        }

        for (String[] interp : interp01) {
            String what = Arrays.stream(interp).map(v -> (v==null) ? "" : v).collect(Collectors.joining("|", "||", ""));
            lines.add(what);
        }

        if (! compare(interp01, interp02)) {
            for (String[] interp : interp02) {
                String what = Arrays.stream(interp).map(v -> (v==null) ? "" : v).collect(Collectors.joining("|", "|exp|", ""));
                lines.add(what);
            }
        }

        return lines;
    }

    static boolean compare(List<String[]> interp01, List<String[]> interp02) {
        if (interp01.isEmpty()  &&  interp02.isEmpty()) {
            return true;
        } else if (interp01.isEmpty()  ||  interp02.isEmpty()) {
            return false;
        } else if (interp01.size() != interp02.size()) {
            return false;
        }

        boolean sameSame = true;
        for (int ndx=0;  sameSame && ndx<interp01.size();  ndx++) {
            String[] data01 = interp01.get(ndx);
            String[] data02 = interp02.get(ndx);
            for (int i=0;  sameSame && i<data01.length;  i++) {
                if (data01[i] == null  &&  data02[i] == null) {
                    // Do nothing here ...
                } else if (data01[i] == null  ||  data02[i] == null) {
                    sameSame = false;
                } else if (! data01[i].equalsIgnoreCase(data02[i])) {
                    sameSame = false;
                }
            }
        }
        return sameSame;
    }
}
