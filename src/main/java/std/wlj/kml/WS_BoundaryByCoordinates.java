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

public class WS_BoundaryByCoordinates {

    static final String BASE_PATH   = "D:/postgis/newberry/rep-boundary";
    static final String WS_ENDPOINT = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/boundaries";
//    static final String WS_ENDPOINT = "http://localhost:8080/std-ws-place/places/reps/";
    static ContentType contentType = ContentType.create("application/vnd.google-earth.kml+xml", "UTF-8");

    static double[][] coordinates = {
        { 40.233845, -111.658531 },
        { 39.1653,   -86.5264 },
        { 25.7617,   -80.1918 },
    };

    public static void main(String... args) throws IOException {
        for (double[] coords : coordinates) {
            processCoordinates(coords);
        }
    }

    static void processCoordinates(double[] latLong) throws IOException {

        String url = WS_ENDPOINT + "?coordinates=" + latLong[0] + "," + latLong[1] +
                                   "&from-year=1820&to-year=1840";
 
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
