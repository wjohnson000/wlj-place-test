package std.wlj.xlit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.familysearch.standards.place.util.PlaceHelper;
import org.json.JSONArray;
import org.json.JSONObject;

public class EnglishToKhmer {

    final static String pathToFiles = "D:/xlit/km";
    final static String inputFile   = "en-names-full.txt";
    final static String outputFile  = "en-to-km-names.txt";

    final static String baseURL     = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=km&dt=t&dt=rm&q=";

    static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(5);
        httpConnManager.setDefaultMaxPerRoute(5);
    }

    static Set<String> found = new HashSet<>();

    public static void main(String...args) throws IOException {
        List<String> xlits = new ArrayList<>();
        List<String> names = Files.readAllLines(Paths.get(pathToFiles, inputFile), Charset.forName("UTF-8"));

      JSONObject jsonOO = new JSONObject("{\"contents\":[[[\"រ៉ូបឺត\",\"Robert\",null,null,3],[null,null,\"rau beut\",\"ˈräbərt\"]],null,\"en\",null,null,null,0.84086442,null,[[\"en\"],null,[0.84086442],[\"en\"]]]}");
      System.out.println(Arrays.toString(extractXlit(jsonOO)));

//        for (String fullName : names) {
//            String[] nameChunks = PlaceHelper.split(fullName, ' ');
//            for (String name : nameChunks) {
//                if (! found.contains(name)) {
//                    found.add(name);
//                    JSONObject jsonO = translate(name);
//                    String[] xlit = extractXlit(jsonO);
//                    if (! xlit[0].isEmpty()  &&  ! xlit[0].equals(xlit[1])) {
//                        String line = Arrays.stream(xlit).collect(Collectors.joining("\t"));
//                        xlits.add(line);
//                        System.out.println(line);
//                    }
//                }
//            }
//        }
//
//        Files.write(Paths.get(pathToFiles, outputFile), xlits, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
    }

    static JSONObject translate(String name) {
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        // Do a GET and parse the results
        HttpGet httpGet = new HttpGet(baseURL + name);
        httpGet.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
//            System.out.println("Name: " + name + " --> " + response.getStatusLine());
            String json = IOUtils.toString(ios, Charset.forName("UTF-8"));
            EntityUtils.consumeQuietly(response.getEntity());
            return new JSONObject("{ \"contents\": " + json + "}");
        } catch (Exception ex) {
            System.out.println("Name: " + name + " --> " + ex.getMessage());
        }

        return null;
    }

    static String[] extractXlit(JSONObject jsonO) {
        String[] result = { "", "", "", "" };

        JSONArray data01 = jsonO.getJSONArray("contents");
        JSONArray data02 = (JSONArray)data01.get(0);
        JSONArray data03 = (JSONArray)data02.get(0);
        JSONArray data04 = (JSONArray)data02.get(1);
        if (data01.length() > 6) {
            Double data06 = data01.getDouble(06);
            System.out.println("data06: " + data06);
        }

        result[0] = data03.getString(1);
        result[1] = data03.getString(0);
        if (data04.length() > 3) {
            result[2] = data04.getString(3);
            result[3] = data04.getString(2);
        } else if (data04.length() > 2) {
            result[2] = data04.getString(2);
            result[3] = data04.getString(2);
        }

        return result;
    }
}
