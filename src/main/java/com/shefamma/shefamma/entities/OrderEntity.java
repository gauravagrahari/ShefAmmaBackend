package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.List;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class OrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public OrderEntity(String order1, String geo11, String geo12, String lunch) {
    }

    public void setUuidItem(String uuidOrder) {
        if (uuidOrder.startsWith("order#")) {
            this.uuidOrder = uuidOrder;
        } else {
            this.uuidOrder = "order#" + uuidOrder;
        }
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidOrder;//uuid will be of guest
    @DynamoDBRangeKey(attributeName = "sk")
    private String timeStamp;
    @DynamoDBIndexHashKey(attributeName = "gpk", globalSecondaryIndexName = "gsi1")
    private String uuidHost;
    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
    private String timeStampGsi;
      @DynamoDBIndexHashKey(attributeName = "gpk2", globalSecondaryIndexName = "gsi2")
    private String uuidDevBoy;
//    @DynamoDBIndexRangeKey(attributeName = "gsk2",globalSecondaryIndexName = "gsi2")
//    private String timeStampGsi;

    @DynamoDBAttribute(attributeName = "name")
    private String name;//this name will be name of the guest
     @DynamoDBAttribute(attributeName = "name2")
    private String nameHost;//this name will be name of the host

    @DynamoDBAttribute(attributeName = "geo1")
    private String geoGuest;
    @DynamoDBAttribute(attributeName = "geo2")
    private String geoHost;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;//new,inProgress,completed,cancelled,failed
    @DynamoDBAttribute(attributeName = "amt")
    private String amount;

    private int itemQuantity;
    @DynamoDBAttribute(attributeName = "nOG")
    private int noOfGuest;
    @DynamoDBAttribute(attributeName = "rat")
    private String rating;
    @DynamoDBAttribute(attributeName="rev")
    private String review;
//    for meals
    @DynamoDBAttribute(attributeName = "meal")
    private String mealType;//breakfast, dinner, lunch


    @DynamoDBAttribute(attributeName = "pkUp")
    private String pickUp;
    @DynamoDBAttribute(attributeName = "stTm")
    private int startTime;
    @DynamoDBAttribute(attributeName = "pyMd")
    private String payMode;
    @DynamoDBAttribute(attributeName = "ordItms")
    private List<OrderedItem> orderedItems;
//    public OrderEntity(String s) {
////        this.s = s;
//    }
}
//{
//        "uuidOrder": "order123",
//        "timeStamp": "2023-06-11T10:00:00.000Z",
//        "hostId": "host123",
//        "status": "placed",
//        "amount": "100.00",
//        "itemQuantity": "3",
//        "noOfGuest": 2,
//
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
