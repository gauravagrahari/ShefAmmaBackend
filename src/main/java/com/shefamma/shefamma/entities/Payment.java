package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class Payment {
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidOrderHost;
    @DynamoDBRangeKey(attributeName = "sk")
    private String timeStamp;
//    @DynamoDBIndexHashKey(attributeName = "gpk", globalSecondaryIndexName = "gsi1")
//    private String hostId;
//    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
//    private String timeStampGsi;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
    @DynamoDBAttribute(attributeName = "pyMd")
    private String payMode;

}
