package std.wlj.aws;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class UploadSolrFoldersS3 {

    private static final String inputDir   = "D:/solr/master-6.5.0/places/data/index";
    private static final String bucketName = "fh-std-artifacts";
    private static final String solrPrefix = "std-ws-place-2/solr";
    private static final String dateStr    = "2017-05-03";
    private static final String folderKey  = solrPrefix + "/" + dateStr;

    public static void main(String... args) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        File baseDir = new File(inputDir);

        for (File file : baseDir.listFiles()) {
            if (file.getName().equals("write.lock")) continue;

            try {
                String fileKey = folderKey + "/" + file.getName();
                System.out.println("Upload: " + fileKey);
                s3Client.putObject(bucketName, fileKey, file);
            } catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException, which means your request made it " +
                        "to Amazon S3, but was rejected with an error response for some reason.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException, which means the client encountered an " +
                        " internal error while trying to communicate with S3, such as not being able to access the network.");
                System.out.println("Error Message: " + ace.getMessage());
            }
        }
    }
}
