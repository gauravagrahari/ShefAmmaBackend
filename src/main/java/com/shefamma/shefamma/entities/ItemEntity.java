package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class ItemEntity {
    public void setUuidItem(String uuidItem) {
        this.uuidItem = uuidItem+"#item";
    }

    @DynamoDBHashKey(attributeName = "pk")
    private String uuidItem;
    @DynamoDBRangeKey(attributeName = "sk")
    @DynamoDBIndexHashKey
    private String nameItem;
    @DynamoDBIndexRangeKey(attributeName = "gsk")
    private String dishcategory;
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
     @DynamoDBAttribute(attributeName = "desc")
    private String description;
    @DynamoDBAttribute(attributeName = "veg")
    private Boolean vegetarian;
    @DynamoDBAttribute(attributeName = "amnt")
    private String amount;
}
