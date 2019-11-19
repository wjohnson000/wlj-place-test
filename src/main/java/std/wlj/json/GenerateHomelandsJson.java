/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.json;

/**
 * @author wjohnson000
 *
 */
public class GenerateHomelandsJson {

    private static String[][] itemDataLots = {
        { "type", "EVENT", "name", "1936 Olympics", "descr", "Munich, Germany" },
        { "type", "EVENT", "name", "Pole Vault", "descr", "throw a long stick" },
        { "type", "EVENT", "name", "Discus", "descr", "throw a round thing" },
        { "type", "EVENT", "name", "Swimming", "descr", "jump in water, try and go fast" },
        { "type", "EVENT", "name", "Basketball", "descr", "get the spherical ball in the round hole" },
        { "type", "THING", "name", "Population", "value", "35,000" },
        { "type", "THING", "name", "Area", "value", "42 sq-km" },
        { "type", "THING", "name", "Altitude", "value", "123 meters" },
    };

    public static void main(String...args) throws Exception {
        for (String[] itemData : itemDataLots) {
            String json = makeJson(itemData);
            System.out.println("\n\n" + json);
        }
    }

    private static String makeJson(String... kvPairs) {
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
