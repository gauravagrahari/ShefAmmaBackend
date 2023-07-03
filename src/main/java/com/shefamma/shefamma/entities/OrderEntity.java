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
    public void setUuidItem(String uuidOrder) {
        if (uuidOrder.startsWith("order#")) {
            this.uuidOrder = uuidOrder;
        } else {
            this.uuidOrder = "order#" + uuidOrder;
        }
    }
//    public void setTimeStampGsi(String timeStampGsi) {
//        this.timeStampGsi = getTimeStamp();
//    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidOrder;
    @DynamoDBRangeKey(attributeName = "sk")
//    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
    private String timeStamp;
    @DynamoDBIndexHashKey(attributeName = "gpk", globalSecondaryIndexName = "gsi1")
    private String hostId;

    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
    private String timeStampGsi;
//    @DynamoDBIndexHashKey(attributeName = "gpk",globalSecondaryIndexName = "gsi1")
//    private String gsiPk;///////this will contain the hostiD
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
//{
//        "uuidOrder": "order123",
//        "timeStamp": "2023-06-11T10:00:00.000Z",
//        "hostId": "host123",
//        "status": "placed",
//        "amount": "100.00",
//        "itemQuantity": "3",
//        "noOfGuest": 2,
//        "rating": "4.5",
//        "review": "Great experience!",
//        "pickUp": "Yes",
//        "startTime": 14,
//        "orderedItems": [
//        {
//        "itemId": "item1",
//        "noOfServing": "2"
//        },
//        {
//        "itemId": "item2",
//        "noOfServing": "1"
//        },
//        {
//        "itemId": "item3",
//        "noOfServing": "3"
//        }
//        ]
//        }
