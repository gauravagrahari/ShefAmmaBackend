package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class OrderEntity  {
    public  void setUuidOrder(String uuidOrder) {
        this.uuidOrder = "order"+uuidOrder;
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidOrder;
    @DynamoDBRangeKey(attributeName = "sk")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "gsisk")
    private String timeStamp;
    @DynamoDBIndexHashKey(attributeName = "gsk", globalSecondaryIndexName = "gsipk")
    private String hostId;

    @DynamoDBAttribute(attributeName = "stts")
    private String status;
    @DynamoDBAttribute(attributeName = "amt")
    private String amount;
    @DynamoDBAttribute(attributeName = "itQuan")
    private String itemQuantity;
    @DynamoDBAttribute(attributeName = "nOG")
    private int noOfGuest;
    @DynamoDBAttribute(attributeName = "rat")
    private String rating;
    @DynamoDBAttribute(attributeName="rev")
    private String review;
    @DynamoDBAttribute(attributeName = "pkUp")
    private String pickUp;
    @DynamoDBAttribute(attributeName = "stTm")
    private int startTime;
    @DynamoDBAttribute(attributeName = "ordItms")
    private List<OrderedItem> orderedItems;


    public OrderEntity(String s) {
//        this.s = s;
    }
}
//    @DynamoDBRangeKey(attributeName = "sk")
//    @DynamoDBIndexHashKey
//    private String timeStamp;
//    @DynamoDBIndexRangeKey(attributeName = "gsk")
//    private String hostId;