/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.cleanup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wjohnson000
 *
 */
public class RunListCollectionDetail {

    public static void main(String...args) {
        String collectionId = "AAAA-wlj";
        CleanupService thisCS = new CleanupService();

        showCollectionDetails(thisCS, collectionId);

        System.exit(0);
    }

    public static void showCollectionDetails(CleanupService thisCS, String collectionId) {
        String[] prevPath = new String[0];
        List<S3File> files = thisCS.getCollectionFiles(collectionId);
        for (S3File file : files) {
            printDirectory(prevPath, file.path);
            String indent = getIndent(file.path.length);
            System.out.println(indent + file.name + "   [" + file.size + "]");
            prevPath = file.path;
        }
    }

    public static void printDirectory(String[] prevDir, String[] thisDir) {
        int len = Math.max(prevDir.length, thisDir.length);

        boolean isDiff = false;
        for (int i=0;  i<len;  i++) {
            String prevChunk = (i < prevDir.length) ? prevDir[i] : "";
            String thisChunk = (i < thisDir.length) ? thisDir[i] : "";
            if ((! prevChunk.equals(thisChunk)  ||  isDiff)  && thisChunk.length() > 0) {
                isDiff = true;
                String indent = getIndent(i);
                System.out.println(indent + thisChunk);
            }
        }
    }

    public static String getIndent(int length) {
        if (length == 0) {
            return "";
        }

        return IntStream.rangeClosed(1, length)
                   .mapToObj(ii -> "    ")
                   .collect(Collectors.joining());
    }
}
