/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.InputStream;
import java.nio.charset.Charset;
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
 * @author wjohnson000
 *
 */
public class ZzzDoInterp {

    static String[] textes = {
        "順帝三年七月七日", 
        "順帝丙寅七月七日", 
        "順帝三年丙寅七月七日", 
        "順帝丙寅三年七月七日", 
        "順帝丙寅叄年七月七日", 
        "順帝丙寅叁年七月七日", 
        "順帝丙寅弎年七月七日", 
        "順帝丙寅四年七月七日", 
        "順帝三年十一月七日", 
        "順帝三年間十一月七日", 
        "順帝三年十一月二十一日", 
        "順帝三年間十一月二十一日", 

        "元順帝三年七月七日", 
        "元帝順帝三年七月七日", 

        "道武帝三年七月七日", 
        "道武帝登國三年七月七日", 
        "道武帝皇始三年七月七日", 
        "登國三年七月七日", 
        "皇始三年七月七日", 
        "魏道武帝三年七月七日", 
        "魏道武帝登國三年七月七日", 
        "魏道武帝皇始三年七月七日", 
    };

//    static String baseUrl = "http://localhost:8080/std-ws-date/dates/interp?text=%s&langHint=zh";
    static String baseUrl = "http://ws.date.std.cmn.beta.us-east-1.test.fslocal.org/dates/interp?text=%s&langHint=zh";
    static String addExp  = "&experiments=use.v2.cjk";

    static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(5);
        httpConnManager.setDefaultMaxPerRoute(5);
    }

    public static void main(String... args) {
        for (String text : textes) {
            String url = String.format(baseUrl, text);
            JSONObject json = pokeDateV2(url);
            List<String> details = getDetails(json, "Date-v1.0");
            details.forEach(System.out::println);

            url += addExp;
            json = pokeDateV2(url);
            details = getDetails(json, "Date-v2.0");
            details.forEach(System.out::println);
            System.out.println();
            System.out.println();
        }
    }

    static JSONObject pokeDateV2(String fullURL) {
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        // Do a GET and parse the results
        try {
            HttpGet httpGet = new HttpGet(fullURL);
            httpGet.addHeader("Accept", "application/json");
            try (CloseableHttpResponse response = client.execute(httpGet);
                    InputStream ios = response.getEntity().getContent()) {
                String json = IOUtils.toString(ios, Charset.forName("UTF-8"));
                EntityUtils.consumeQuietly(response.getEntity());
                return new JSONObject("{ \"contents\": " + json + "}");
            }
        } catch(Exception ex) {
            // Do nothing ...
        }

        return null;
    }

    static List<String> getDetails(JSONObject json, String title) {
        List<String> details = new ArrayList<>();
        JSONObject contents = json.getJSONObject("contents");
        JSONArray dates = contents.getJSONArray("dates");

        boolean addOrig = true;
        for (int ndx=0;  ndx<dates.length();  ndx++) {
            details.add(getDetail(dates.getJSONObject(ndx), title, addOrig));
            addOrig = false;
        }

        return details;
    }

    static String getDetail(JSONObject json, String title, boolean addOrig) {
        StringBuilder buff = new StringBuilder();

        buff.append((addOrig)? json.getString("original") : "");
        buff.append("|").append(title);
        buff.append("|\"").append(json.getString("gedcomx")).append("\"");
        buff.append("|").append(json.getJSONObject("interp").get("assisted"));

        JSONObject detail = json.getJSONObject("detail");
        if (detail != null) {
            JSONArray sDates = detail.getJSONArray("simpleDates");
            if (sDates != null  &&  sDates.length() > 0) {
                buff.append("|").append(sDates.getJSONObject(0).get("astroday"));
            }
        }

        return buff.toString();
    }
}
