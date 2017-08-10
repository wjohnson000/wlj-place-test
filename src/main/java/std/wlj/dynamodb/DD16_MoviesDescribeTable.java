// Copyright 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache License, Version 2.0.
package std.wlj.dynamodb;

import java.util.stream.Collectors;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class DD16_MoviesDescribeTable {

    public static void main(String[] args) throws Exception {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        String tableName = "Movies";

        System.out.println("Describing " + tableName);

        TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
        System.out.format(
            "Name: %s:\n" +
            "  Status: %s \n" +
            "  Item Count: %d \n" +
            "  Table ARN: %s \n" + 
            "  Key Schema: %s \n" + 
            "  Attributes: %s \n" + 
            "  Provisioned Throughput (read capacity units/sec): %d \n" +
            "  Provisioned Throughput (write capacity units/sec): %d \n",
            tableDescription.getTableName(),
            tableDescription.getTableStatus(),
            tableDescription.getItemCount(),
            tableDescription.getTableArn(),
            tableDescription.getKeySchema().stream().map(key -> key.getAttributeName() + "::" + key.getKeyType()).collect(Collectors.joining(",")),
            tableDescription.getAttributeDefinitions().stream().map(attr -> attr.getAttributeName() + "::" + attr.getAttributeType()).collect(Collectors.joining(",")),
            tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
            tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
    }
}