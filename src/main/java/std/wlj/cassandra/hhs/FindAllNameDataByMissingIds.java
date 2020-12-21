/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.familysearch.jencoding.JEncoding;

/**
 * @author wjohnson000
 *
 */
public class FindAllNameDataByMissingIds {

    public static void main(String...args) throws Exception {
        Set<String> knownIds   = getKnownIds();
        Set<String> missingIds = new TreeSet<>();
        Set<String> maybeIds   = new HashSet<>();

        JEncoding jEncoding = new JEncoding();
        for (long id=0;  id<1_000_000L;  id++) {
            String jId = jEncoding.encode(id);
            if (knownIds.contains(jId)) {
                missingIds.addAll(maybeIds);
                maybeIds.clear();
            } else {
                maybeIds.add(jId);
            }
        }

        System.out.println("MissingIds.size=" + missingIds.size());
        Files.write(Paths.get("C:/temp/dev-name-missing-ids.txt"), missingIds, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static Set<String> getKnownIds() throws Exception {
        List<String> nameIds = Files.readAllLines(Paths.get("C:/temp/dev-name-ids-all.txt"), StandardCharsets.UTF_8);
        System.out.println("ID.count=" + nameIds.size());
        return new HashSet<>(nameIds);
    }
}
