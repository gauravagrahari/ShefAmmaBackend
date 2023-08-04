package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
    public TimeSlotEntity(String uuidTime) {
    }
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

    public boolean isValidTimeGap() {
        if (slots == null || slots.isEmpty()) {
            return true; // No slots to check, so the time gap is valid.
        }

        int durationInt = Integer.parseInt(duration);

        for (int i = 1; i < slots.size(); i++) {
            SlotSubEntity previousSlot = slots.get(i - 1);
            SlotSubEntity currentSlot = slots.get(i);
            int timeGap = currentSlot.getStartTime() - (previousSlot.getStartTime() + durationInt);
            if (timeGap < 0) {
                return false; // Invalid time gap, return false and show an error.
            }
        }

        return true; // All time gaps are valid.
    }
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
//    _________________
//public TimeSlotEntity(String uuidTime, String duration, int capacity) {
//    setUuidTime(uuidTime);
//    this.duration = duration;
//    this.capacity = capacity;
//    this.slots = new ArrayList<>();
//}
}

//{
//        "uuidTime": "time#123456789",
//        "duration": "60",
//        "capacity": 10,
//        "slots": [
//        {
//        "startTime": 9,
//        "currentCapacity": 10
//        },
//        {
//        "startTime": 11,
//        "currentCapacity": 5
//        },
//        {
//        "startTime": 14,
//        "currentCapacity": 8
//        }
//        ]
//        }

