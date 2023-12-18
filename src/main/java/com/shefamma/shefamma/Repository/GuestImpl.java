package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.AdressSubEntity;
import com.shefamma.shefamma.entities.GuestEntity;
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
        return guestentity;
    }
    @Override
    public GuestEntity getGuestAddress(String guestId, String nameGuest) {
        return dynamoDBMapper.load(GuestEntity.class, guestId, nameGuest);
    }

    public AdressSubEntity getGuestAddress(String uuidGuest) {
        DynamoDBQueryExpression<GuestEntity> queryExpression = new DynamoDBQueryExpression<GuestEntity>()
                 .withConsistentRead(false)
                .withKeyConditionExpression("pk = :val")
                .withExpressionAttributeValues(Map.of(":val", new AttributeValue().withS(uuidGuest)))
                .withProjectionExpression("adr"); 

        List<GuestEntity> guests = dynamoDBMapper.query(GuestEntity.class, queryExpression);
        if (!guests.isEmpty()) {
            GuestEntity retrievedGuest = guests.get(0);
            return retrievedGuest.getAddressGuest();
        }
        return null;
    }

    
    public GuestEntity updateGuest(String partition, String sort,String attributeName,GuestEntity guestEntity) {
        String value = switch (attributeName) {
            case "uuidGuest" -> guestEntity.getUuidGuest();
            case "geocode" -> guestEntity.getGeocode();
            case "name" -> guestEntity.getName();
            case "dob" -> guestEntity.getDob();
            case "gender" -> guestEntity.getGender();
            default -> throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        };

        commonMethods.updateAttribute(partition,attributeName,value);
        return guestEntity;
    }

    @Override
    public GuestEntity getGuestUsingPk(String pk) {
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