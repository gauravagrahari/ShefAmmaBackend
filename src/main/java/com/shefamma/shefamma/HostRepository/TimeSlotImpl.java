package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.SlotSubEntity;
import com.shefamma.shefamma.entities.TimeSlotEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TimeSlotImpl implements TimeSlot{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private CommonMethods commonMethods;
    @Autowired
    private SlotSubEntity slotSubEntity;
    @Override
    public TimeSlotEntity saveSlotTime(TimeSlotEntity timeentity) {
         dynamoDBMapper.save(timeentity);
        return timeentity;
    }

    @Override
    public TimeSlotEntity getTimeSlot(String timehostId) {
        return dynamoDBMapper.load(TimeSlotEntity.class,timehostId);
    }

    @Override
    public TimeSlotEntity updateTimeSlot(String partition, TimeSlotEntity timeentity) {
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
            .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)));

        dynamoDBMapper.save(timeentity, saveExpression);
            return timeentity;
    }

    @Override
    public TimeSlotEntity updateAttributeTimeSlot(String partition, String attributeName, TimeSlotEntity timeentity) {
        String value = null;
        // Get the value of the specified attribute
        switch (attributeName) {
            case "descriptionHost":
                value = String.valueOf(slotSubEntity.getStartTime());
                break;
            case "currentMessage":
                value = String.valueOf(slotSubEntity.getCurrentCapacity());
                break;
            // Add more cases for other attributes if needed
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
//        commonMethods.updateAttribute(partition,attributeName,value);
//need to create a map function to update multiple nested jsons
            return timeentity;
    }

}

//public GuestEntity updateGuest(String partition, String sort, GuestEntity guestEntity) {
//    DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
//            .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)))
//            .withExpectedEntry("sk", new ExpectedAttributeValue(new AttributeValue(sort)));
//
//    dynamoDBMapper.save(guestEntity, saveExpression);
//
//    return guestEntity;
//}