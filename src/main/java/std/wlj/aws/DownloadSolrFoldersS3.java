package std.wlj.aws;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class DownloadSolrFoldersS3 {

    private static final String outputDir  = "C:/temp/solr-index";
    private static final String bucketName = "fh-std-artifacts";
    private static final String solrPrefix = "std-ws-place-2/solr";
    private static final String dateStr    = "2017-04-20";

    public static void main(String... args) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        ObjectListing objListing = s3Client.listObjects(bucketName, solrPrefix + "/" + dateStr);

        List<S3ObjectSummary> fileSummaries = objListing.getObjectSummaries();
        for (S3ObjectSummary fileSummary : fileSummaries) {
            try {
                S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileSummary.getKey()));
                String fileName = getFileName(s3Object.getKey());
                System.out.println(s3Object.getKey() + " --> " + fileName + " --> " + s3Object.getObjectMetadata().getContentLength() + " .. " + s3Object.getObjectMetadata().getContentType());
                InputStream inStr = s3Object.getObjectContent();
                Files.copy(inStr, Paths.get(outputDir, fileName), StandardCopyOption.REPLACE_EXISTING);
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
            } catch (IOException ioe) {
                System.out.println("Unable to save the file ..." + ioe.getMessage());
            }
        }
    }

    static String getFileName(String key) {
        int ndx = key.lastIndexOf('/');
        return (ndx < 0) ? key : key.substring(ndx+1);
    }
}
