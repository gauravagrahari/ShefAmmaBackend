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
        if (uuidItem.startsWith("item#")) {
            this.uuidItem = uuidItem;
        } else {
            this.uuidItem = "item#" + uuidItem;
        }
    }
    public void setNameItem(String nameItem) {
        
        this.nameItem = nameItem.toLowerCase();
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidItem;
    @DynamoDBRangeKey(attributeName = "sk")
    private String nameItem;
    @DynamoDBAttribute(attributeName = "gsk")
    private String dishcategory;
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
     @DynamoDBAttribute(attributeName = "dsec")
    private String description;
    @DynamoDBAttribute(attributeName = "veg")
    private String vegetarian;
    @DynamoDBAttribute(attributeName = "amnt")
    private String amount;
    @DynamoDBAttribute(attributeName = "ser")
    private String serveQuantity;
    @DynamoDBAttribute(attributeName = "serTy")
    private String serveType;
    @DynamoDBAttribute(attributeName = "spIng")
    private String specialIngredient;
}
