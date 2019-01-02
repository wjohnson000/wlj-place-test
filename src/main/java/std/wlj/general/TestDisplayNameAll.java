/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.place.access.validator.MessageFactory;
import org.familysearch.standards.place.access.validator.NameValidator;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class TestDisplayNameAll {

    public static void main(String...args) throws Exception {
        List<String> displayNames = Files.readAllLines(Paths.get("C:/temp/db-dump/display-name-all.txt"), StandardCharsets.UTF_8);
        System.out.println("LINES: " + displayNames.size());
        List<String> errors = new ArrayList<>(100_000);
        Map<String, Integer> badChars = new TreeMap<>();

        NameValidator validr = new NameValidator(null, new MessageFactory());
        for (String dispName : displayNames) {
            String[] name = PlaceHelper.split(dispName, '|');
            if (name.length > 5  &&  name[1].isEmpty()  &&  "f".equals(name[5])) {
                try {
                    validr.validateDisplayName(name[3]);
                } catch (Exception ex) {
                    String message = ex.getMessage().trim();
                    System.out.println(dispName + " --> " + message);
                    errors.add(dispName + "|" + message);

                    int ndx = message.lastIndexOf(' ');
                    String temp = message.trim().substring(ndx);
                    temp = temp.replace('"', ' ').replace(']', ' ').trim();

                    while (temp.length() < 4) temp = "0" + temp;
                    temp = "U+" + temp.toUpperCase();
                    Integer count = badChars.getOrDefault(temp, new Integer(0));
                    badChars.put(temp, count+1);
                }
            }
        }

        Files.write(Paths.get("C:/temp/db-dump/display-name-errors.txt"), errors, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("\n\n");
        for (Map.Entry<String, Integer> entry : badChars.entrySet()) {
            String temp = entry.getKey().substring(2);
            while (temp.charAt(0) == '0') temp = temp.substring(1);
            int intVal = Integer.parseInt(temp, 16);
            char chVal = (char)intVal;
            String sCH = String.valueOf(chVal);
            String nCH = Character.getName(intVal);
            System.out.println(entry.getKey() + "|" + entry.getValue() + "|" + sCH + "|" + nCH);
        }
    }
}
