package std.wlj.kml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class WS_CreateBoundaries {

    static final String BASE_PATH   = "D:/postgis/newberry/rep-boundary-us";
    static final String WS_ENDPOINT = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/reps/";
//    static final String WS_ENDPOINT = "http://localhost:8080/std-ws-place/places/reps/";
    static ContentType contentType = ContentType.create("application/vnd.google-earth.kml+xml", "UTF-8");

    public static void main(String... args) throws IOException {
        File baseDir = new File(BASE_PATH);
        for (String fileName : baseDir.list()) {
            processFile(fileName);
        }
    }

    static void processFile(String fileName) throws IOException {
        int ndx = fileName.indexOf('-');
        String repId = fileName.substring(0, ndx);

        byte[] contents = Files.readAllBytes(Paths.get(BASE_PATH, fileName));
        String kml = new String(contents);
        if (kml.charAt(0) != '<') {
            kml = kml.substring(1);
        }

        String url = WS_ENDPOINT + repId + "/boundaries";
        System.out.println("URL: " + url + " --> byte-count:" + kml.length());

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(kml, contentType);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = client.execute(httpPost);
            System.out.println("  RE: " + response);
            System.out.println("   S: " + response.getStatusLine());
            System.out.println("   H: " + Arrays.toString(response.getAllHeaders()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
