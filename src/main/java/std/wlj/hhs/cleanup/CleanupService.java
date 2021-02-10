/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.cleanup;

import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;

/**
 * @author wjohnson000
 *
 */
public class CleanupService {

    private static final String bucketName = "ps-services-us-east-1-074150922133-homelands-admin";

    private AmazonS3 s3Client;

    public CleanupService() {
        s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    public List<String> getCollectionIds() {
        Set<String> results = new TreeSet<>();

        ListObjectsRequest ListRequest =
                new ListObjectsRequest()
                       .withBucketName(bucketName)
                       .withPrefix("collection")
                       .withMaxKeys(1000);

        boolean hasMore = true;
        ObjectListing objListing = s3Client.listObjects(ListRequest);
        while (hasMore) {
            System.out.println("Next chunk size: " + objListing.getObjectSummaries().size() + " ... " + objListing.getNextMarker());

            for (S3ObjectSummary fileSummary : objListing.getObjectSummaries()) {
                if (fileSummary.getKey().startsWith("collection")) {
                    String key = fileSummary.getKey().substring(11);
                    int ndx = key.indexOf('/');
                    if (ndx > 0) {
                        key = key.substring(0, ndx);
                        results.add(key);
                    }
                }
            }

            if (objListing.getNextMarker() == null  ||  objListing.getObjectSummaries().isEmpty()) {
                hasMore = false;
            } else {
                ListRequest.setMarker(objListing.getNextMarker());
                objListing = s3Client.listObjects(ListRequest);
            }
        }
        
        return new ArrayList<>(results);
    }

    public List<S3File> getCollectionFiles(String collectionId) {
        List<S3File> results = new ArrayList<>();

        ListObjectsRequest ListRequest =
                new ListObjectsRequest()
                       .withBucketName(bucketName)
                       .withPrefix("collection/" + collectionId)
                       .withMaxKeys(1000);


        boolean hasMore = true;
        ObjectListing objListing = s3Client.listObjects(ListRequest);
        while (hasMore) {
            System.out.println("Next chunk size: " + objListing.getObjectSummaries().size() + " ... " + objListing.getNextMarker());

            for (S3ObjectSummary fileSummary : objListing.getObjectSummaries()) {
                System.out.println(">>> " + fileSummary.getKey());
                String[] pathName = PlaceHelper.split(fileSummary.getKey(), '/');
                S3File file = new S3File();
                file.name = pathName[pathName.length - 1];
                file.size = fileSummary.getSize();
                if (pathName.length > 2) {
                    file.path = new String[pathName.length-2];
                    System.arraycopy(pathName, 1, file.path, 0, pathName.length-2);
                    results.add(file);
                }
            }

            if (objListing.getNextMarker() == null  ||  objListing.getObjectSummaries().isEmpty()) {
                hasMore = false;
            } else {
                ListRequest.setMarker(objListing.getNextMarker());
                objListing = s3Client.listObjects(ListRequest);
            }
        }

        return results;
    }

    public boolean deleteCollection(String collectionId) {
        List<String> keys = new ArrayList<>();

        ListObjectsRequest ListRequest =
                new ListObjectsRequest()
                       .withBucketName(bucketName)
                       .withPrefix("collection/" + collectionId)
                       .withMaxKeys(1000);


        boolean hasMore = true;
        ObjectListing objListing = s3Client.listObjects(ListRequest);
        while (hasMore) {
            System.out.println("Next chunk size: " + objListing.getObjectSummaries().size() + " ... " + objListing.getNextMarker());

            for (S3ObjectSummary fileSummary : objListing.getObjectSummaries()) {
                keys.add(fileSummary.getKey());
            }

            if (objListing.getNextMarker() == null  ||  objListing.getObjectSummaries().isEmpty()) {
                hasMore = false;
            } else {
                ListRequest.setMarker(objListing.getNextMarker());
                objListing = s3Client.listObjects(ListRequest);
            }
        }

        if (keys.isEmpty()) {
            return true;
        } else {
            DeleteObjectsRequest deleteRequest =
                                    new DeleteObjectsRequest(bucketName)
                                    .withKeys(keys.toArray(new String[0]));
            DeleteObjectsResult result = s3Client.deleteObjects(deleteRequest);
            return result.getDeletedObjects().size() == keys.size();
        }
    }
}
