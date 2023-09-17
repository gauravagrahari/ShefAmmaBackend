package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.AccountEntity;
import com.shefamma.shefamma.entities.AdressSubEntity;
import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.HostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GuestImpl implements Guest {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private CommonMethods commonMethods;

    @Override
    public GuestEntity saveGuest(GuestEntity guestentity) {
        dynamoDBMapper.save(guestentity);
        System.out.println(guestentity);
        return guestentity;
    }
//    @Override
//    public GuestEntity updateGuest(String guestId, String nameGuest, List<String> attributeName, GuestEntity guestentity) {
//        dynamoDBMapper.save(guestentity);
//        System.out.println(guestentity);
//        return guestentity;
//    }
    @Override
    public GuestEntity getGuestAddress(String guestId, String nameGuest) {
        return dynamoDBMapper.load(GuestEntity.class, guestId, nameGuest);
    }

    public AdressSubEntity getGuestAddress(String uuidGuest) {
        DynamoDBQueryExpression<GuestEntity> queryExpression = new DynamoDBQueryExpression<GuestEntity>()
                 .withConsistentRead(false)
                .withKeyConditionExpression("pk = :val")
                .withExpressionAttributeValues(Map.of(":val", new AttributeValue().withS(uuidGuest)))
                .withProjectionExpression("adr"); // Specify the attributes you want to retrieve

        List<GuestEntity> guests = dynamoDBMapper.query(GuestEntity.class, queryExpression);
        if (!guests.isEmpty()) {
            GuestEntity retrievedGuest = guests.get(0);
            return retrievedGuest.getAddressGuest();
        }
        return null;
    }

    //    Use the below code if update condition also involves sort key
    public GuestEntity updateGuest(String partition, String sort,String attributeName,GuestEntity guestEntity) {
        String value = null;
        // Get the value of the specified attribute
        switch (attributeName) {
            case "uuidGuest":
                value = guestEntity.getUuidGuest();
                break;
            case "geocode":
                value = guestEntity.getGeocode();
                break;
            case "name":
                value = guestEntity.getName();
                break;
//            case "DP":
//                value = guestEntity.getDP();
//                break;
            case "dob":
                value = guestEntity.getDob();
                break;
            case "gender":
                value = guestEntity.getGender();
                break;
            // Add more cases for other attributes if needed
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        commonMethods.updateAttribute(partition,attributeName,value);
        return guestEntity;
    }

    @Override
    public GuestEntity getGuestUsingPk(String pk) {
//        GuestEntity hashKeyValues = new GuestEntity();
//        hashKeyValues.setUuidGuest(pk);
//
//
//        DynamoDBQueryExpression<GuestEntity> queryExpression = new DynamoDBQueryExpression<GuestEntity>()
//                .withHashKeyValues(hashKeyValues);
//
//        List<GuestEntity> result = dynamoDBMapper.query(GuestEntity.class, queryExpression);
//
//        return result.isEmpty() ? null : result.get(0);
//    }
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pk", new AttributeValue().withS(pk));

        DynamoDBQueryExpression<GuestEntity> queryExpression = new DynamoDBQueryExpression<GuestEntity>()
                .withKeyConditionExpression("pk = :pk")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(1);

        List<GuestEntity> users = dynamoDBMapper.query(GuestEntity.class, queryExpression);
        return users.isEmpty() ? null : users.get(0);
    }
}