package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
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
    @DynamoDBHashKey(attributeName = "pk")
    private String guestId_Order;
    @DynamoDBRangeKey(attributeName = "sk")
    private String timeStamp;
    @DynamoDBAttribute(attributeName = "gs1sk1")
    private String hostId;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
    @DynamoDBAttribute(attributeName = "amt")
    private String amount;
    @DynamoDBAttribute(attributeName = "itQuan")
    private String itemQuantity;
    @DynamoDBAttribute(attributeName = "nOG")
    private String noOfGuest;
    @DynamoDBAttribute(attributeName = "ordItms")
    private List<OrderedItem> orderedItems;


    public OrderEntity(String s) {
//        this.s = s;
    }
}
