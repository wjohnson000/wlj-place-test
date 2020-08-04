/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ws.httpclient;

import java.util.HashMap;
import java.util.Map;

/**
 * Create, Read, Update, Read and Delete a dummy name from the "MMMM-RRH" collection on 'dev'.  The "dev" environment
 * is used since it requires authentication (and authorization) for all POST, PUT and DELETE operations.
 * 
 * @author wjohnson000
 *
 */
public class TestClientsCRUD {

    static String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org/name";

    static String NAME_JSON_SQ =
           "{" +
           "    'id': 'MMMC-N4X'," +
           "    'name': 'Béatrix'," +
           "    'collectionId': 'MMMM-RRH'," +
           "    'visibility': 'PUBLIC'," +
           "    'type': 'FIRST'," +
           "    'language': 'fr'," +
           "    'originLanguage': 'fr'," +
           "    'definition': {" +
           "        'formattedString': [" +
           "            {" +
           "                'type': 'PARAGRAPH'," +
           "                'content': [" +
           "                    {" +
           "                        'style': 'HTML'," +
           "                        'text': 'Beatrix <i>[bienheureuse, en latin]</i>, martyre à Rome, avec ses frères saint Simplice et saint Faustin, pendant la persécution de Dioclétien, au quatrième siècle, honorée le 29 juillet. Ses reliques sont dans L-église de Sainte-Marie-Majeure, à Rome. L-église honore aussi, le 10 mai, la bienheureuse Béatrix d-Este, morte à Padoue en 1226.'" +
           "                    }" +
           "                ]" +
           "            }" +
           "        ]" +
           "    }," +
           "    'variants': {}," +
           "    'createUserId': 'cis.user.MMMM-8QZD'," +
           "    'createDate': '2020-07-10T19:47:05.040Z'," +
           "    'modifyUserId': 'cis.user.MMMM-8QZD'," +
           "    'modifyDate': '2020-07-10T19:47:05.040Z'" +
           
           "}";
    static String NAME_JSON = NAME_JSON_SQ.replace('\'', '"');

    static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Accept-Language", "fr");
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Authorization", "Bearer " + "???????????????????????????????????????????");
    }

    public static void main(String... args) throws Exception {
        doItRawJava();
        doItApacheClient();
        doItApacheClientPooled();
        doItSpringWebClient();
    }

    static void doItRawJava() throws Exception {
        String      nameId;
        WebResponse response;

        JavaClient javaClient = JavaClient.getDefault("application/json", headers);
        System.out.println("\n\n>>>>> RAW JAVA >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        System.out.println("STEP 1: read a known name ...");
        response = javaClient.doGet(BASE_URL + "?text=Masse&type=LAST&format=html");
        showResult(response);

        System.out.println("STEP 2: create a new name ...");
        response = javaClient.doPost(BASE_URL, NAME_JSON);
        showResult(response);
        nameId = getNameId(response);
        System.out.println("  >> nmid: " + nameId);

        if (nameId != null) {
            System.out.println("STEP 3: read the newly-created name");
            response = javaClient.doGet(BASE_URL + "/" + nameId);
            showResult(response);

            System.out.println("STEP 4: delete the newly-created name");
            response = javaClient.doDelete(BASE_URL + "/" + nameId);
            showResult(response);
        }
    }

    static void doItApacheClient() throws Exception {
        String      nameId;
        WebResponse response;

        ApacheClient apacheClient = ApacheClient.getDefault("application/json", headers);
        System.out.println("\n\n>>>>> APACHE CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        System.out.println("STEP 1: read a known name ...");
        response = apacheClient.doGet(BASE_URL + "?text=Masse&type=LAST&format=html");
        showResult(response);

        System.out.println("STEP 2: create a new name ...");
        response = apacheClient.doPost(BASE_URL, NAME_JSON);
        showResult(response);
        nameId = getNameId(response);
        System.out.println("  >> nmid: " + nameId);

        if (nameId != null) {
            System.out.println("STEP 3: read the newly-created name");
            response = apacheClient.doGet(BASE_URL + "/" + nameId);
            showResult(response);

            System.out.println("STEP 4: delete the newly-created name");
            response = apacheClient.doDelete(BASE_URL + "/" + nameId);
            showResult(response);
        }
    }

    static void doItApacheClientPooled() throws Exception {
        String      nameId;
        WebResponse response;

        ApacheClientPooled apacheClient = ApacheClientPooled.getDefault("application/json", headers);
        System.out.println("\n\n>>>>> APACHE CLIENT POOLED >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        System.out.println("STEP 1: read a known name ...");
        response = apacheClient.doGet(BASE_URL + "?text=Masse&type=LAST&format=html");
        showResult(response);

        System.out.println("STEP 2: create a new name ...");
        response = apacheClient.doPost(BASE_URL, NAME_JSON);
        showResult(response);
        nameId = getNameId(response);
        System.out.println("  >> nmid: " + nameId);

        if (nameId != null) {
            System.out.println("STEP 3: read the newly-created name");
            response = apacheClient.doGet(BASE_URL + "/" + nameId);
            showResult(response);

            System.out.println("STEP 4: delete the newly-created name");
            response = apacheClient.doDelete(BASE_URL + "/" + nameId);
            showResult(response);
        }
    }

    static void doItSpringWebClient() throws Exception {
        String      nameId;
        WebResponse response;

        SpringWebClient springClient = SpringWebClient.getDefault("application/json", headers);
        System.out.println("\n\n>>>>> SPRING WEB CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        System.out.println("STEP 1: read a known name ...");
        response = springClient.doGet(BASE_URL + "?text=Masse&type=LAST&format=html");
        showResult(response);

        System.out.println("STEP 2: create a new name ...");
        response = springClient.doPost(BASE_URL, NAME_JSON);
        showResult(response);
        nameId = getNameId(response);
        System.out.println("  >> nmid: " + nameId);

        if (nameId != null) {
            System.out.println("STEP 3: read the newly-created name");
            response = springClient.doGet(BASE_URL + "/" + nameId);
            showResult(response);

            System.out.println("STEP 4: delete the newly-created name");
            response = springClient.doDelete(BASE_URL + "/" + nameId);
            showResult(response);
        }
    }

    static void showResult(WebResponse response) {
        System.out.println("  >> stts: " + response.getStatus());
        response.getHeaders().entrySet().forEach(hh -> System.out.println("  >> head: " + hh));
        System.out.println("  >> name: " + response.getBody());
        if (response.getException() != null) {
            System.out.println("  >> excp: " + response.getException().getMessage());
        }
    }

    static String getNameId(WebResponse response) {
        String locationHdr = response.getHeaders().getOrDefault("LOCATION", response.getHeaders().getOrDefault("Location", ""));
        int ndx = locationHdr.lastIndexOf('/');
        return (ndx == -1) ? null : locationHdr.substring(ndx+1);
    }
}
