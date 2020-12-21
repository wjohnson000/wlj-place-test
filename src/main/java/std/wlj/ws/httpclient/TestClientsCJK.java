/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ws.httpclient;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Create, Read, Update, Read and Delete a dummy name from the "MMMM-RRH" collection on 'dev'.  The "dev" environment
 * is used since it requires authentication (and authorization) for all POST, PUT and DELETE operations.
 * 
 * @author wjohnson000
 *
 */
public class TestClientsCJK {

    static String BASE_URL = "http://core.homelands.service.integ.us-east-1.dev.fslocal.org/name";

    static String NAME_JSON_SQ =
           "{" +
           "    'id': 'MMMC-N4X'," +
           "    'name': '冠廷'," +
           "    'collectionId': 'MMMM-938'," +
           "    'visibility': 'PUBLIC'," +
           "    'type': 'FIRST'," +
           "    'language': 'en'," +
           "    'originLanguage': 'en'," +
           "    'definition': {" +
           "        'formattedString': [" +
           "            {" +
           "                'type': 'PARAGRAPH'," +
           "                'content': [" +
           "                    {" +
           "                        'style': 'HTML'," +
           "                        'text': '北京 世界您好 䣺䮊^休息日' " +
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
    static String NAME_JSON = NAME_JSON_SQ.replace('\'', '"').replace('^', (char)0x3000);

    static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Accept-Language", "en");
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Authorization", "Bearer " + "???????????????????????????????????????????");
    }

    public static void main(String... args) throws Exception {
        String what = "北京 世界您好 䣺䮊　休息日";
        what.codePoints().forEach(cp -> System.out.println("  cp: " + Integer.toHexString(cp) + "." + Character.isSpaceChar((char)cp)));
        System.out.println();
        doItApacheClient();
    }

    static void doItApacheClient() throws Exception {
        String      nameId;
        WebResponse response;

        ApacheClient apacheClient = ApacheClient.getDefault("application/json", headers);
        System.out.println("\n\n>>>>> APACHE CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        System.out.println("STEP 1A: read a known name ...");
        response = apacheClient.doGet(BASE_URL + "?text=Masse&type=LAST&format=html");
        showResult(response);

        System.out.println("STEP 1Bz: read an unknown name ...");
        String queryStr2 = BASE_URL + "?text=" + URLEncoder.encode("䣺䮊^休".replace('^', (char)0x3000), "UTF-8") + "&type=LAST&format=html";
        System.out.println("  >> query=" + queryStr2);
        response = apacheClient.doGet(queryStr2);
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
