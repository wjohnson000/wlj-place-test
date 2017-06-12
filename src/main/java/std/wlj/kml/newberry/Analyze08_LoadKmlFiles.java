package std.wlj.kml.newberry;

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

public class Analyze08_LoadKmlFiles {

    static final String PATH_TO_LOAD = "D:/postgis/newberry/rep-boundary";

    static final String WS_ENDPOINT  = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/reps/";
    static final ContentType CONTENT_TYPE = ContentType.create("application/vnd.google-earth.kml+xml", "UTF-8");

    public static void main(String... args) {
        File loadDir = new File(PATH_TO_LOAD);
        String[] kmlFiles = loadDir.list();
        Arrays.stream(kmlFiles).forEach(kmlFile -> loadKmlFile(kmlFile));
    }

    static void loadKmlFile(String kmlFile) {
        System.out.println("Processing file: " + kmlFile);

        try {
            int ndx = kmlFile.indexOf('-');
            String repId = kmlFile.substring(0, ndx);
            byte[] contents = Files.readAllBytes(Paths.get(PATH_TO_LOAD, kmlFile));
            String kml = new String(contents);
            if (kml.charAt(0) != '<') {
                kml = kml.substring(1);
            }

            String url = WS_ENDPOINT + repId + "/boundaries";
            System.out.println("URL: " + url + " --> byte-count:" + kml.length());

            try(CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(url);
                StringEntity entity = new StringEntity(kml, CONTENT_TYPE);
                httpPost.setEntity(entity);
                CloseableHttpResponse response = client.execute(httpPost);
                System.out.println("  RE: " + response);
                System.out.println("   S: " + response.getStatusLine());
                System.out.println("   H: " + Arrays.toString(response.getAllHeaders()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch(Exception ex) {
            System.out.println("Unable to load file '" + kmlFile + "' -- " + ex.getMessage());
        }
    }
}
