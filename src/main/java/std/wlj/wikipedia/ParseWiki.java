package std.wlj.wikipedia;

import java.nio.charset.Charset;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

public class ParseWiki {

    private static String[] urls = {
        "https://en.wikipedia.org/wiki/Barranquilla_Colombia_Temple",
        "https://en.wikipedia.org/wiki/Bakersfield,_Missouri#History",  
        "https://en.wikipedia.org/wiki/Hebrides",
        "https://sk.wikipedia.org/wiki/Pre%C5%A1ovsk%C3%BD_kraj",
        "https://en.wikipedia.org/wiki/Maine",
        "https://fr.wikipedia.org/wiki/13e_arrondissement_de_Paris"
    };
    
    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(2);
        httpConnManager.setDefaultMaxPerRoute(2);
    }

    public static void main(String...args) {
        for (String url : urls) {
            String page  = getURLContents(url);
            String wikiX = parseWikiBlunt(page);
            String wikiY = WikiSaxHandler.parseWikiSAX(page);
            String wikiZ = WikiSaxHandler.parseWikiUrlSAX(url);
            System.out.println("00 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("\n" + wikiX + "\n\n");
            System.out.println("   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("\n" + removeHtmlJericho(wikiX) + "\n\n");
            System.out.println("   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("\n" + wikiY + "\n\n");
            System.out.println("   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("\n" + wikiZ + "\n\n");
            System.out.println("   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }

        httpConnManager.close();
        System.exit(0);
    }

    static String getURLContents(String url) {
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);  //NOSONAR

        // GET the request
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            System.out.println("Response from WIKI: " + response.getStatusLine());
            return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        } catch (Exception ex) {
            System.out.println("Unable to GET results: " + ex.getMessage());
            return "";
        }
    }

    static String parseWikiBlunt(String wikiAll) {
        int idx0 = wikiAll.indexOf("<body");
        int idxT = wikiAll.indexOf("<table", idx0);
        int idxP = wikiAll.indexOf("<p>", idx0);
        if (idxT > 0  &&  idxT < idxP) {
            int tableCnt = 1;
            while(tableCnt > 0) {
                int idx1 = wikiAll.indexOf("<table", idxT+1);
                int idx2 = wikiAll.indexOf("</table", idxT+1);
                if (idx1 > 0  &&  idx1 < idx2) {
                    tableCnt++;
                    idxT = idx1 + 1;
                } else if (idx2 > 0  &&  idx2 < idx1) {
                    tableCnt--;
                    idxT = idx2 + 1;
                } else {
                    System.out.println("YIKES!!");
                }
            }
            idxP = wikiAll.indexOf("<p>", idxT);
        }

        if (idxP < 0) {
            return "";
        } else {
            System.out.println("P0: " + idxP);
            int idxP1 = wikiAll.indexOf("<p>", idxP+1);
            System.out.println("P1: " + idxP1);
            int idxP2 = wikiAll.indexOf("</p>", idxP1+1);
            System.out.println("P2: " + idxP2);
            return wikiAll.substring(idxP, idxP2);
        }
    }

    static String removeHtmlJericho(String textWithHtml) {
        Source htmlSource = new Source(textWithHtml);
        Segment htmlSegment = new Segment(htmlSource, 0, htmlSource.length());
        Renderer htmlRenderer = new Renderer(htmlSegment);
        return htmlRenderer.toString();
    }
}
