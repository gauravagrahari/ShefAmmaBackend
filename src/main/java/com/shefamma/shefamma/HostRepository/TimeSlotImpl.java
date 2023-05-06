package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.TimeSlotEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TimeSlotImpl implements TimeSlot{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Override
    public TimeSlotEntity saveSlotTime(TimeSlotEntity timeentity) {
         dynamoDBMapper.save(timeentity);
        return timeentity;
    }

    @Override
    public TimeSlotEntity getTimeSlot(String timehostId, TimeSlotEntity timeentity) {
        return dynamoDBMapper.load(TimeSlotEntity.class,timehostId);
    }

    @Override
    public TimeSlotEntity updateTimeSlot(String partition, TimeSlotEntity timeentity) {
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
            .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)));

        dynamoDBMapper.save(timeentity, saveExpression);
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