package std.wlj.kml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class WS_BoundaryContainsReps {

    static final String BASE_PATH   = "D:/postgis/newberry/rep-boundary";
    static final String WS_ENDPOINT = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/boundaries/contains";
//    static final String WS_ENDPOINT = "http://localhost:8080/std-ws-place/places/reps/";
    static ContentType contentType = ContentType.create("application/vnd.google-earth.kml+xml", "UTF-8");

    static int[] boundaryIds = { 1, 11, 111, 1111, 11111 };

    public static void main(String... args) throws IOException {
        for (int boundaryId : boundaryIds) {
            processBoundaryContains(boundaryId);
        }
    }

    static void processBoundaryContains(int boundaryId) throws IOException {

        String url = WS_ENDPOINT + "?boundary-id=" + boundaryId +
                                   "&rep-ids=22,222,2222,22222,222222,2222222,33,333,3333,33333,333333,3333333,392250,392429,395589,10266087,10625001";
 
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            long time0 = System.nanoTime();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = client.execute(httpGet);
            long time1 = System.nanoTime();

            System.out.println("\n=========================================================================");
            System.out.println(" URL: " + url);
            System.out.println("   X: " + (time1 - time0) / 1_000_000.0);
            System.out.println(" RES: " + response);
            System.out.println("   S: " + response.getStatusLine());
            System.out.println("   H: " + Arrays.toString(response.getAllHeaders()));
            if (response.getEntity() != null) {
                System.out.println("   T: " + response.getEntity().getContentType());
                
                System.out.println(IOUtils.readLines(response.getEntity().getContent(), Charset.forName("UTF-8")).stream()
                        .collect(Collectors.joining("\n", "\n", "")));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
