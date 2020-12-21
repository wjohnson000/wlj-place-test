/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.aws;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

/**
 * @author wjohnson000
 *
 */
public class DownloadFile {

    static final String BUCKET = "ps-services-us-east-1-074150922133-homelands-admin";
    static final String FILE   = "collection/unassigned/geneanet-first-small.csv";

    public static void main(String...args) {
        S3Client s3Client = S3Client.builder()
                                .region(Region.US_EAST_1)
                                .credentialsProvider(DefaultCredentialsProvider.create())
                                .build();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(BUCKET).key(FILE).build();

        try {
            ResponseBytes<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());
            System.out.println(new String(s3Object.asByteArray()));
        } catch (NoSuchKeyException ex) {
            System.out.println("EX: " + ex);
        }
    }
}
