package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class OrderedItem {
    @DynamoDBAttribute(attributeName="name")
    private String itemName;
    @DynamoDBAttribute(attributeName="amt")
    private String itemPrice;
    @DynamoDBAttribute(attributeName="nOS")
    private String noOfServing;
    
}
