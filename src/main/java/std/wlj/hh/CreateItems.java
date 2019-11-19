/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hh;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author wjohnson000
 *
 */
public class CreateItems {

    private static String      hhLocalURL  = "http://localhost:8080/hhs/items";
    private static String      hhAwsURL    = "http://ws.homelands.service.integ.us-east-1.dev.fslocal.org/items";
    private static ContentType contentType = ContentType.create("application/json", "UTF-8");

    public static void main(String...args) {
        String[][] itemDataSource = {
            { "type", "EVENT", "name", "1936 Olympics", "descr", "Munich, Germany" },
            { "type", "EVENT", "name", "Pole Vault", "descr", "throw a long stick" },
            { "type", "EVENT", "name", "Discus", "descr", "throw a round thing" },
            { "type", "EVENT", "name", "Swimming", "descr", "jump in water, try and go fast" },
            { "type", "EVENT", "name", "Basketball", "descr", "get the spherical ball in the round hole" },
            { "type", "THING", "name", "Population", "value", "35,000" },
            { "type", "THING", "name", "Area", "value", "42 sq-km" },
            { "type", "THING", "name", "Altitude", "value", "123 meters" },
        };

        for (String[] itemData : itemDataSource) {
            String json = makeJson(itemData);
            createItem(hhAwsURL, json);
        }
    }

    static void createItem(String appURL, String json) {
        // POST the request, but don't show any concern about the response
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(appURL);
            StringEntity entity = new StringEntity(json, contentType);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = client.execute(httpPost);
            System.out.println("Resp: " + response.getStatusLine());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static String makeJson(String... kvPairs) {
        StringBuilder buff = new StringBuilder();
        buff.append("{ ");

        for (int i=0;  i<kvPairs.length;  i+=2) {
            if (buff.length() > 2) {
                buff.append(", ");
            }
            buff.append('"').append(kvPairs[i]).append('"').append(": ");
            buff.append('"').append(kvPairs[i+1]).append('"');
        }

        buff.append(" }");

        return buff.toString();
    }
}
