package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class ItemEntity {
    @DynamoDBHashKey(attributeName = "pk")
    private String hostId_Item;//h_id
    @DynamoDBRangeKey(attributeName = "sk")
    private String nameItem;//hName
    @DynamoDBAttribute(attributeName = "ctgry")
    private String category;//hEmail
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;//hPhone
    @DynamoDBAttribute(attributeName = "stts")
    private String status;//hPhone
     @DynamoDBAttribute(attributeName = "desc")
    private String description;
    @DynamoDBAttribute(attributeName = "veg")
    private String vegetarian;
    @DynamoDBAttribute(attributeName = "amnt")
    private String amount;//hPhone
}
