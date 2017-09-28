package std.wlj.analysis.queries;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.familysearch.standards.analysis.model.InterpretationModel;
import org.familysearch.standards.analysis.model.ResultRepModel;
import org.familysearch.standards.analysis.model.RootModel;
import org.familysearch.standards.analysis.model.SearchResultsModel;
import org.familysearch.standards.loader.AppConstants;
import org.familysearch.standards.place.util.PlaceHelper;
import org.springframework.http.HttpStatus;

public class ProcessMostUsed {

    private static int  READ_TIME_OUT    = 120000;
    private static int  CONNECT_TIME_OUT = 30000;

    private static String inFile  = "D:/pcas/most-used-text.log";
    private static String outFile = "D:/pcas/most-used-details";
//    private static String baseURL = "http://familysearch.org/int-std-ws-analysis/interpretation/request/";
    private static String baseURL = "http://localhost:8080/std-ws-analysis/interpretation/request/";

    /** XML object mapper */
    public static Unmarshaller xmlUnmarshaller;
    static {
        try {
            JAXBContext context = JAXBContext.newInstance(RootModel.class);
            xmlUnmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) { }
    }


    public static void main(String... args) throws IOException {
        // Read the data, throwing away the header lines ...
        List<String> rawData = Files.readAllLines(Paths.get(inFile), Charset.forName("UTF-8"));
        rawData.remove(0);
        rawData.remove(0);

        int count = 0;
        List<String> details = new ArrayList<>();
        for (String line : rawData) {
            count++;
            System.out.println(count + " --> " + line);
            if (count % 200 == 0) {
                Files.write(Paths.get(outFile + "-" + count + ".txt"), details, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            String[] chunks = PlaceHelper.split(line, '|');
            if (chunks.length > 1) {
                int requestId = Integer.parseInt(chunks[0].trim());
                int usedCount = Integer.parseInt(chunks[1].trim());
                details.addAll(processRequest(requestId, usedCount));
            }
        }

        Files.write(Paths.get(outFile + "-" + count + ".txt"), details, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<String> processRequest(int requestId, int usedCount) {
        List<String> details = new ArrayList<>();
        try {
            String url = baseURL + requestId;
            String response = getResponse(url);
            RootModel root = fromXML(response);
            if (root == null) {
                details.add(requestId + "|" + usedCount + "|" + response);
            } else {
                details = mapResponse(requestId, usedCount, root.getSearchResults());
            }
        } catch(Exception ex) {
            details.add(requestId + "|" + usedCount + "|" + ex.getMessage());
        }
        return details;
    }

    static HttpURLConnection createURLConnection(URL serviceURL) throws IOException {
        URLConnection urlConn = serviceURL.openConnection();
        HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;

        httpUrlConn.setRequestProperty("Accept-Language", "en");
        httpUrlConn.setRequestProperty("Accept-Charset", "utf-8");
        httpUrlConn.setRequestProperty("Accept", "application/xml");

        httpUrlConn.setRequestMethod(AppConstants.METHOD_GET);
        httpUrlConn.setReadTimeout(READ_TIME_OUT);
        httpUrlConn.setConnectTimeout(CONNECT_TIME_OUT);

        return httpUrlConn;
    }

    static String getResponse(String url) {
        String response = "";

        try {
            HttpURLConnection urlConn = createURLConnection(new URL(url));
            HttpURLConnection.setFollowRedirects(true);
            urlConn.connect();

            int status = urlConn.getResponseCode();
            if (status == HttpStatus.OK.value()) {
                response = IOUtils.toString(urlConn.getInputStream(), StandardCharsets.UTF_8);
            } else {
                response = "Error." + status;
            }
        } catch (IOException e) {
            response = "Exception." + e.getMessage();
        }

        return response;
    }

    static RootModel fromXML(String xmlString) {
        try {
            StringReader reader = new StringReader(xmlString);
            return (RootModel)xmlUnmarshaller.unmarshal(reader);
        } catch (JAXBException | NullPointerException e) {
            System.out.println("EE: " + e.getMessage());
            return null;
        }
    }
    
    static List<String> mapResponse(int requestId, int usedCount, SearchResultsModel srsModel) {
        List<String> lines = new ArrayList<>();

        if (srsModel == null  ||  srsModel.getResults() == null  ||  srsModel.getResults().isEmpty()) {
            StringBuilder buff = new StringBuilder();
            buff.append(requestId).append("|").append(usedCount).append("|No results found");
            lines.add(buff.toString());
        } else {
            boolean first = true;
            for (InterpretationModel interpModel : srsModel.getResults()) {
                StringBuilder buff = new StringBuilder();
                if (first) {
                    first = false;
                    buff.append(requestId).append("|").append(usedCount);
                } else {
                    buff.append("|");
                }
                buff.append("|").append(interpModel.getPlaceName());
                buff.append("|").append(String.valueOf(interpModel.getParameters()));

                buff.append("|").append(interpModel.getTotalRepCount());
                buff.append("|").append(interpModel.getAnnotations().stream().collect(Collectors.joining(",")));

                for (ResultRepModel repModel : interpModel.getResultReps()) {
                    buff.append("|").append(repModel.getRepId());
                    buff.append("|").append(repModel.getRelevanceScore());
                    buff.append("|").append(repModel.getInterpLang());
                    buff.append("|").append(repModel.getParseLang());
                }
                lines.add(buff.toString());
            }
        }

        return lines;
    }
}
