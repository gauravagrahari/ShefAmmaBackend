package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
        if (uuidTime.startsWith("time#")) {
            this.uuidTime = uuidTime;
        } else {
            this.uuidTime = "time#" + uuidTime;
        }
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidTime;
    @DynamoDBRangeKey(attributeName = "sk")
    private String duration;
    @DynamoDBAttribute(attributeName = "cap")
    private int capacity;

    @DynamoDBAttribute(attributeName = "slots")
    private List<SlotSubEntity> slots;
    public void resetCurrentCapacityIfNecessary() {
        // Get the current hour of the day in UTC
        int currentHour = LocalDateTime.now(ZoneOffset.UTC).getHour();

        for (SlotSubEntity slot : slots) {
            double startTime = slot.getStartTime();
            double endTime = startTime + Integer.parseInt(duration);

            // Check if the current time has reached the end of the time slot
            if (currentHour >= endTime) {
                // Reset the current capacity to the initial capacity
                slot.setCurrentCapacity(capacity);
            }
        }
    }
}
