/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.aws;

import java.io.File;
import java.util.List;

/**
 * @author wjohnson000
 *
 */
public class TestS3Helper {

    public static void main(String... args) {
        List<String> folders = S3Helper.getFolders();
        folders.forEach(ff -> System.out.println("  Folder: " + ff));

        List<String> keys = S3Helper.getKeys(null);
        keys.forEach(kk -> System.out.println("   kk1: " + kk));

        keys = S3Helper.getKeys("lts-2019-05-21");
        keys.forEach(kk -> System.out.println("   kk2: " + kk));
//
//        boolean isDelOK = S3Helper.deleteS3Folder("wlj-2019-06-11-A");
//        System.out.println("\n\nDel-OK? " + isDelOK);
//
//        String tempLocation = "C:/temp/delete-me";
//        File tempFile = new File(tempLocation);
//        tempFile.mkdirs();
//        boolean isDownloadOK = S3Helper.downloadFiles("C:/temp/solr-home/places/data/index", "lts-2019-05-30", tempLocation);
//        System.out.println("\n\nDownload-OK? " + isDownloadOK);
//
//        boolean isCopyOK = S3Helper.copyFilesToFinalLocation(tempLocation, "C:/temp/solr-home/places/data/index");
//        System.out.println("\n\nCopy-OK? " + isCopyOK);
//
//        boolean isOK01 = S3Helper.uploadFiles("C:/temp/no-files-here", "wlj-2019-06-11-A");
//        System.out.println("\nOK? " + isOK01);

        long time0 = System.nanoTime();
        boolean isOK02 = S3Helper.uploadFiles("C:/D-Drive/solr/backup", "wlj-2019-06-11-A");
        System.out.println("\nOK? " + isOK02);
        long time1 = System.nanoTime();
        System.out.println("Time: " + (time1 - time0) / 1_000_000.0);
    }
}
