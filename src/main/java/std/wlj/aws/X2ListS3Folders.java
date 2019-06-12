/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * @author wjohnson000
 *
 */
public class X2ListS3Folders {

    private static final String s3Bucket  = "ps-services-us-east-1-074150922133";
    private static final String stdPrefix = "s3/standards-solr";

    public static void main(String...args) {
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .build();

        ListObjectsRequest listReq = new ListObjectsRequest()
                .withBucketName(s3Bucket)
                .withPrefix(stdPrefix);

        ObjectListing listRes = s3Client.listObjects(listReq);
        for (S3ObjectSummary objectSummary : listRes.getObjectSummaries()) {
            System.out.printf(" - %s (ssss: %d)\n", objectSummary.getKey(), objectSummary.getSize());
        }

        System.out.println();
        ListObjectsV2Request listV2Req = new ListObjectsV2Request()
                .withBucketName(s3Bucket)
                .withPrefix(stdPrefix)
                .withMaxKeys(11);
        ListObjectsV2Result listV2Res;

        do {
            listV2Res = s3Client.listObjectsV2(listV2Req);
            for (S3ObjectSummary objectSummary : listV2Res.getObjectSummaries()) {
                System.out.printf(" - %s (zzzz: %d)\n", objectSummary.getKey(), objectSummary.getSize());
            }

            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            String token = listV2Res.getNextContinuationToken();
            System.out.println("Next Continuation Token: " + token);
            listV2Req.setContinuationToken(token);

        } while (listV2Res.isTruncated());
    }
}
