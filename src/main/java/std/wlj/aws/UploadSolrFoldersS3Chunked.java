package std.wlj.aws;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class UploadSolrFoldersS3Chunked {

    private static final String inputDir   = "D:/solr/master-6.5.0/places/data/index";
    private static final String bucketName = "fh-std-artifacts";
    private static final String solrPrefix = "std-ws-place-2/solr";
    private static final String dateStr    = "2017-05-03";
    private static final String folderKey  = solrPrefix + "/" + dateStr;

    public static void main(String... args) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        s3Client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());

        File baseDir = new File(inputDir);

        long time0 = System.nanoTime();
        for (File file : baseDir.listFiles()) {
            if (file.getName().equals("write.lock")) continue;
            updateFile(s3Client, file);
        }
    }

    static void updateFile(AmazonS3 s3Client, File file) {
        String fileKey = folderKey + "/" + file.getName();
        System.out.println("Upload: " + fileKey);

        List<PartETag> partETags = new ArrayList<>();
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, fileKey);
        InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.
        try {
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                partSize = Math.min(partSize, (contentLength - filePosition));
                
                UploadPartRequest uploadRequest = new UploadPartRequest()
                    .withBucketName(bucketName).withKey(fileKey)
                    .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                    .withFileOffset(filePosition)
                    .withFile(file)
                    .withPartSize(partSize);

                partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            CompleteMultipartUploadRequest compRequest = new 
                        CompleteMultipartUploadRequest(bucketName, fileKey, initResponse.getUploadId(), partETags);
            s3Client.completeMultipartUpload(compRequest);
        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    bucketName, fileKey, initResponse.getUploadId()));
        }
    }
}
