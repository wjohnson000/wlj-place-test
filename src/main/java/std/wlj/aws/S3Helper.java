/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.aws;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.familysearch.standards.core.logging.Logger;

/**
 * A helper class for AWS S3 operations.  All operations are against the Place 2.0 private portion
 * of the shared bucket.  These operations include:
 * <ul>
 *   <li>List all keys in the Place 2.0 bucket</li>
 *   <li>List all keys in a folder within the Place 2.0 bucket</li>
 *   <li>List all folders within the Place 2.0 bucket</li>
 *   <li>Upload files from the local file system into a new Place 2.0 folder</li>
 *   <li>Download files from S3 bucket into a local directory</li>
 *   <li>Delete all files in a Place 2.0 folder</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class S3Helper {
    
    private static final Logger LOGGER = new Logger(S3Helper.class);
    private static final String MODULE_NAME = "AwsS3Helper";
    private static final int    S3_LIST_BATCH_SIZE = 1000;

    private static final String s3Bucket  = "ps-services-us-east-1-074150922133";
    private static final String stdPrefix = "s3/standards-solr";

    /** Create a private constructor since this is a utility class only */
    private S3Helper() { }

    public static List<String> getKeys(String folderName) {
        return getKeys(folderName, getAmazonS3());
    }

    /**
     * List all top-level "folders" in the Standards-Solr "folder".
     * 
     * @return list of top-level "folder" names
     */
    public static List<String> getFolders() {
        List<String> folders = new ArrayList<>();

        List<String> keys = getKeys(null, getAmazonS3());
        for (String key : keys) {
            int ndx = key.indexOf('/');
            if (ndx > 0) {
                String folder = key.substring(0, ndx);
                if (! folders.contains(folder)) {
                    folders.add(folder);
                }
            }
        }

        return folders;
    }

    /**
     * Upload all files in a local directory to AWS S3 bucket
     * 
     * @param pathToLocalFiles path where the files reside on the local file system
     * @param folderName virtual folder name where files will reside in S3
     * 
     * @return TRUE if the operation succeeded, FALSE otherwise
     */
    public static boolean uploadFiles(String pathToLocalFiles, String folderName) {
        boolean isOK = false;
        TransferManager xferManager = getTransferManager();

        File localFolder = new File(pathToLocalFiles);
        if (localFolder == null  ||  ! localFolder.exists()  ||  ! localFolder.isDirectory()) {
            LOGGER.info(null, MODULE_NAME, "Invalid path to local files ... " + pathToLocalFiles);
        } else {
            LOGGER.info(null, MODULE_NAME, "Uploading Files to " + folderName);

           long startTime = System.currentTimeMillis();
             try {
                String folderPrefix = stdPrefix + "/" + folderName;
                MultipleFileUpload xfer = xferManager.uploadDirectory(s3Bucket, folderPrefix, localFolder, false);
                while (! xfer.isDone()) {
                    try {
                        Thread.sleep(2000L);
                    } catch(InterruptedException ex) { // NOSONAR
                        LOGGER.info(null, MODULE_NAME, "Unable to sleep ...");
                    }
                }

                isOK = true;
            } catch (AmazonServiceException e) {
                LOGGER.info(e, MODULE_NAME, "Upload: something went worng ... wrnog ... rong ... rwong ... whatever");
            } finally {
                xferManager.shutdownNow(true);
                LOGGER.info(null, MODULE_NAME, "Update directory finished",
                        "ElapsedTime", String.valueOf(System.currentTimeMillis() - startTime));
            }
        }

        return isOK;
    }

    /**
     * Download files from an S3 folder into a target directory.  This is a two-step process since the
     * download from S3 will create unwanted intervening sub-directories.
     * 
     * @param pathToLocalFiles path were files are to be saved
     * @param folderName virtual S3 folder name where files are currently located
     * @param tempDir intermediate directory that will hold files before being moved to final location
     * 
     * @return TRUE if the operation succeeded, FALSE otherwise
     */
    public static boolean downloadFiles(String pathToLocalFiles, String folderName, String tempDir) {
        boolean step01 = downloadFilesToTemp(folderName, tempDir);
        boolean step02 = moveFilesToFinalLocation(tempDir, pathToLocalFiles);

        return step01 & step02;
    }

    /**
     * Delete all files from a virtual folder in AWS S3.
     * 
     * @param folderName the folder name, which is really a file prefix
     * @return TRUE if the operation succeeded, FALSE otherwise
     */
    public static boolean deleteS3Folder(String folderName) {
        boolean isOK = false;

        try {
            AmazonS3 amazonS3 = getAmazonS3();
            List<String> keys = getKeys(folderName, amazonS3);
            for (String key : keys) {
                String objName = stdPrefix + "/" + folderName + "/" +  key;
                amazonS3.deleteObject(s3Bucket, objName);
            }

            isOK = true;
        } catch(AmazonServiceException e) {
            LOGGER.info(e, MODULE_NAME, "Delete: something went worng ... wrnog ... rong ... rwong ... whatever");
        }

        return isOK;
    }

    /**
     * @return a {@link AmazonS3} instance for all operations.
     */
    protected static AmazonS3 getAmazonS3() {
        return AmazonS3ClientBuilder.standard().build();
    }

    /**
     * @return a {@link TransferManager} instance for all operations.
     */
    protected static TransferManager getTransferManager() {
        return TransferManagerBuilder.standard().build();
    }

    /**
     * Retrieve all files (keys) in the private space of the shared bucket, either at the top level
     * or in a virtual folder.  For folders with many keys, the results may be batched, so it's
     * necessary to loop through the results.
     * 
     * @param folderName folder name (list files with that prefix) or null (list all files in the
     *        top-level of the shared bucket.
     * @param amazonS3 Amazon S3 instance to use
     * @return list of all keys in the given context
     */
    protected static List<String> getKeys(String folderName, AmazonS3 amazonS3) {
        List<String> keys = new ArrayList<>(1024);

        String folderPrefix = (folderName == null) ? stdPrefix : stdPrefix + "/" + folderName;
        ListObjectsV2Request listV2Req = new ListObjectsV2Request()
                .withBucketName(s3Bucket)
                .withPrefix(folderPrefix)
                .withMaxKeys(S3_LIST_BATCH_SIZE);
        ListObjectsV2Result listV2Res;

        boolean moreFiles = true;
        while (moreFiles) {
            listV2Res = getAmazonS3().listObjectsV2(listV2Req);
            for (S3ObjectSummary objectSummary : listV2Res.getObjectSummaries()) {
                String key = objectSummary.getKey();
                key = key.substring(folderPrefix.length()+1).trim();  // Allow for trailing slash character
                if (! key.isEmpty()) {
                    keys.add(key);
                }
            }

            String token = listV2Res.getNextContinuationToken();
            listV2Req.setContinuationToken(token);
            moreFiles = (token != null);
        }
        
        return keys;
    }

    /**
     * Download files from an S3 "virtual" folder to a local file system.  The "toLocation" will be
     * the root directory of a set of nested folders.
     * 
     * @param folderName S3 folder name
     * @param toLocation location on the file system where S3 files are to be copied
     * 
     * @return TRUE if the operation succeeded, FALSE otherwise
     */
    protected static boolean downloadFilesToTemp(String folderName, String toLocation) {
        boolean isOK = false;

        File toFile = new File(toLocation);
        TransferManager xferManager = getTransferManager();

        long startTime = System.currentTimeMillis();
        try {
            String folderPrefix = stdPrefix + "/" + folderName;
            MultipleFileDownload fileDownload = xferManager.downloadDirectory(s3Bucket, folderPrefix, toFile);
            while (! fileDownload.isDone()) {
                LOGGER.info(null, MODULE_NAME, "Copying Files ...");
                try {
                    Thread.sleep(2000L);
                } catch(InterruptedException ex) { // NOSONAR
                    LOGGER.info(null, MODULE_NAME, "Unable to sleep ...");
                }
            }
            isOK = true;
        } catch (AmazonServiceException e) {
            LOGGER.info(e, MODULE_NAME, "Upload: something went worng ... wrnog ... rong ... rwong ... whatever");
        } finally {
            xferManager.shutdownNow();
            LOGGER.info(null, MODULE_NAME, "Download file finished",
                    "ElapsedTime", String.valueOf(System.currentTimeMillis() - startTime));

        }
        return isOK;
    }

    /**
     * Move all files from a given location to the Solr Index.  Note that the "from" location directory
     * structure will be flattened, i.e., the directory structure will not be preserved.
     * 
     * @param fromLocation location of the files to copy
     * @param toLocation location where the files are to be copied
     * 
     * @return TRUE if the operation succeeded, FALSE otherwise
     */
    protected static boolean moveFilesToFinalLocation(String fromLocation, String toLocation) {
        boolean isOK = false;

        final Path fromPath = Paths.get(fromLocation);
        try (Stream<Path> stream = Files.walk(fromPath)) {
            stream.forEachOrdered(nextPath -> {
                File fromFile = nextPath.toFile();
                if (! fromFile.isDirectory()) {
                    Path toPath = Paths.get(toLocation, fromFile.getName());
                    try {
                        Path newPath = Files.move(nextPath, toPath);
                        LOGGER.info("File move: " + fromFile + " --> " + newPath);
                    } catch(Exception ex) {
                        LOGGER.info(ex, MODULE_NAME, "Unable to move a file from: " + fromFile + " --> " + toPath);
                    }
                }
            });
            isOK = true;
        } catch(IOException ex) {
            LOGGER.info(ex, MODULE_NAME, "Unable to move files to: " + toLocation);
        }

        return isOK;
    }

}
