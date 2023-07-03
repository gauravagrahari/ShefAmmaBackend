package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    @Override
    public GuestEntity getGuest(String guestId, String nameGuest) {
        return dynamoDBMapper.load(GuestEntity.class, guestId, nameGuest);
    }

    public GuestEntity getGuest(String uuidGuest) {
        GuestEntity guest = new GuestEntity();
        guest.setUuidGuest(uuidGuest);

        DynamoDBQueryExpression<GuestEntity> queryExpression = new DynamoDBQueryExpression<GuestEntity>()
                 .withConsistentRead(false)
                .withKeyConditionExpression("pk = :val")
                .withExpressionAttributeValues(Map.of(":val", new AttributeValue().withS(uuidGuest)))
                .withProjectionExpression("adr"); // Specify the attributes you want to retrieve

        List<GuestEntity> guests = dynamoDBMapper.query(GuestEntity.class, queryExpression);
        if (!guests.isEmpty()) {
            GuestEntity retrievedGuest = guests.get(0);
            guest.setAddressGuest(retrievedGuest.getAddressGuest());
            return guest;
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
}