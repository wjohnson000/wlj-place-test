package std.wlj.aws.homelands;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class UploadFiles {

    private static final String inputDir   = "C:/temp";
    private static final String bucketName = "ps-services-us-east-1-074150922133-homelands-admin";
    private static final String s3Prefix   = "wayne/test";

    private static final String[] fileNames = {
        "blah.txt",
        "chinese-month-names.docx",
        "sinocal-emp-reigns.txt",
        "std-ws-place-schema.txt",
    };


    public static void main(String... args) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        File baseDir = new File(inputDir);

        for (String fileName : fileNames) {
             try {
                 File file = new File(inputDir, fileName);
                 String fileKey = s3Prefix + "/" + fileName;
                 System.out.println("Upload: " + fileKey);
                 s3Client.putObject(bucketName, fileKey, file);
             }
             catch (AmazonServiceException ase) {
                 System.out.println("Caught an AmazonServiceException, which means your request made it " +
                                         "to Amazon S3, but was rejected with an error response for some reason.");
                 System.out.println("Error Message:    " + ase.getMessage());
                 System.out.println("HTTP Status Code: " + ase.getStatusCode());
                 System.out.println("AWS Error Code:   " + ase.getErrorCode());
                 System.out.println("Error Type:       " + ase.getErrorType());
                 System.out.println("Request ID:       " + ase.getRequestId());
             }
             catch (AmazonClientException ace) {
                 System.out.println("Caught an AmazonClientException, which means the client encountered an " +
                                         " internal error while trying to communicate with S3, such as not being able to access the network.");
                 System.out.println("Error Message: " + ace.getMessage());
             }
        }
    }
}
