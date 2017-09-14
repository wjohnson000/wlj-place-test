package std.wlj.xlit;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleTranslateUtil {

    final static String EN_TO_KM_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=km&dt=t&dt=rm&q=";
    final static String KM_TO_EN_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=km&tl=en&dt=t&dt=rm&q=";

    static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(5);
        httpConnManager.setDefaultMaxPerRoute(5);
    }

    public static String[] enToKm(String englishName) {
        String url = EN_TO_KM_URL + englishName;
        JSONObject jsonO = pokeGoogle(url);
        String[]   results = extractXlit(jsonO);
        return new String[] { results[0], results[1] };
    }

    public static String[] kmToEn(String khmerName) {
        String url = KM_TO_EN_URL + khmerName;
        JSONObject jsonO = pokeGoogle(url);
        String[]   results = extractXlit(jsonO);
        return new String[] { results[0], results[2] };
    }

    static JSONObject pokeGoogle(String fullURL) {
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

    static String[] extractXlit(JSONObject jsonO) {
        String[] result = { "", "", "", "" };

        try {
            JSONArray data01 = jsonO.getJSONArray("contents");
            JSONArray data02 = (JSONArray)data01.get(0);

            JSONArray data03 = (JSONArray)data02.get(0);
            result[0] = data03.getString(1);
            result[1] = data03.getString(0);

            JSONArray data04 = (JSONArray)data02.get(1);
            if (data04.length() > 3) {
                result[2] =String.valueOf(data04.get(3));
                result[3] = String.valueOf(data04.get(2));
            } else if (data04.length() > 2) {
                result[2] = String.valueOf(data04.get(2));
                result[3] = String.valueOf(data04.get(2));
            }
        } catch(Exception ex) {
            // Do nothing
        }
        return result;
    }

}
