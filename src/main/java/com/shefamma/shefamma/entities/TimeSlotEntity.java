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
        this.uuidTime = "time#"+uuidTime;
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidTime;
    @DynamoDBRangeKey(attributeName = "sk")
    private String duration;
    @DynamoDBAttribute(attributeName = "slots")
    private List<SlotSubEntity> slots;
}

