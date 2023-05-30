package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class TimeSlotEntity {

    public void setUuidTime(String uuidTime) {
        this.uuidTime = uuidTime+"time";
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidTime;
    @DynamoDBRangeKey(attributeName = "sk")
    private String duration;
    @DynamoDBAttribute(attributeName = "slots")
    private List<SlotSubEntity> slots;
}

//    @DynamoDBHashKey(attributeName = "pk")
//    private String hostId_Time;//time#hostId
//
//    public void setTimehostId(String timehostId) {
//        this.hostId_Time =timehostId+"#time";
//    }
////    @DynamoDBAttribute(attributeName = "sk")
////    private String day;//hName
//    @DynamoDBAttribute(attributeName = "stts")
//    private String status;//hEmail
//    @DynamoDBAttribute(attributeName = "startTime")
//    private String startTime;
//    @DynamoDBAttribute(attributeName = "endTime")
//    private String endTime;
//    @DynamoDBAttribute(attributeName = "cap")
//    private String capacity;
//    -----------------------
