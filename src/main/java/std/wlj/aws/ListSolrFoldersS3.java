package std.wlj.aws;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class ListSolrFoldersS3 {

    private static final String bucketName = "fh-std-artifacts";

    public static void main(String... args) {
//        AmazonS3 s3Client = new AmazonS3Client();
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        ObjectListing objListing = s3Client.listObjects(bucketName);

        List<String> prefixes = objListing.getCommonPrefixes();
        for (String prefix : prefixes) {
            System.out.println("PFX: " + prefix);
        }

        List<String> keys = new ArrayList<>();
        List<S3ObjectSummary> fileSummaries = objListing.getObjectSummaries();
        for (S3ObjectSummary fileSummary : fileSummaries) {
            keys.add(fileSummary.getKey());
            System.out.println("FS: key=" + fileSummary.getKey() + " . size=" + fileSummary.getSize());
        }

        System.out.println("\n\n=======================================");
        printAll("", keys, 0);
    }

    public static void printAll(String base, List<String> keys, int indent) {
        List<String> dirs = getS3Directories(base, keys);
        List<String> files = getS3Files(base, keys);

        String sIndent = "                                                         ".substring(0, indent*4);
        for (String dir : dirs) {
            System.out.println(sIndent + "[" + dir + "]");
        }
        for (String file : files) {
            System.out.println(sIndent + ">" + file);
        }

        for (String dir : dirs) {
            printAll(base + "/" + dir, keys, indent+1);
        }
    }

    /**
     * Return a list of "directory" names relative to a given basePath and a list of S3 object
     * keys.  For example, given the following input:
     * <pre>
     *   basePath=/base
     *   objectKeys=/base/one/image01.jpg
     *              /base/one/image02.jpg
     *              /base/one/image03.jpg
     *              /base/two/imageAA.jpg
     *              /base/two/imageBB.jpg
     * </pre>
     * The method would return a list with "one" and "two".
     * 
     * @param basePath base path
     * @param objectKeys list of object keys, which sorta' look like path names
     * @return
     */
    public static List<String> getS3Directories(String basePath, List<String> objectKeys) {
        Set<String> dirNames = new TreeSet<>();
        String normalizedBase = FilenameUtils.normalize(basePath, true);
        for (String objectKey : objectKeys) {
            String[] dirAndFile = getDirAndFile(normalizedBase, objectKey);
            if (! dirAndFile[0].isEmpty()) {
                dirNames.add(dirAndFile[0]);
            }
        }

        return new ArrayList<String>(dirNames);
    }

    /**
     * Return a list of "file" names relative to a given basePath and a list of S3 object
     * keys.  For example, given the following input:
     * <pre>
     *   basePath=/base/one
     *   objectKeys=/base/one/image01.jpg
     *              /base/one/image02.jpg
     *              /base/one/image03.jpg
     *              /base/two/imageAA.jpg
     *              /base/two/imageBB.jpg
     * </pre>
     * The method would return a list with "image01.jpg", "image02.jpg" and "image03.jpg".
     * 
     * @param basePath base path
     * @param objectKeys list of object keys, which sorta' look like path names
     * @return
     */
    public static List<String> getS3Files(String basePath, List<String> objectKeys) {
        List<String> fileNames = new ArrayList<>();
        String normalizedBase = FilenameUtils.normalize(basePath, true);
        for (String objectKey : objectKeys) {
            String[] dirAndFile = getDirAndFile(normalizedBase, objectKey);
            if (! dirAndFile[1].isEmpty()) {
                fileNames.add(dirAndFile[1]);
            }
        }

        return fileNames;
    }

    /**
     * Determine the path (directory) and file-name related to a given normalized base path.
     * 
     * @param basePath normalized base path
     * @param objectKey the full "path" for an object in S3
     * @return
     */
    protected static String[] getDirAndFile(String basePath, String objectKey) {
        String dir = "";
        String file = "";

        String normalizedName = FilenameUtils.normalize(objectKey, true);
        int ndx = normalizedName.indexOf(basePath);
        if (ndx >= 0) {
            String tempName = normalizedName.substring(basePath.length());
            if (tempName.startsWith("/")) {
                tempName = tempName.substring(1);
            }
            ndx = tempName.indexOf("/");
            if (ndx < 0) {
                file = tempName;
            }
            else {
                dir = tempName.substring(0, ndx);
            }
        }

        return new String[] { dir, file };
    }
}
