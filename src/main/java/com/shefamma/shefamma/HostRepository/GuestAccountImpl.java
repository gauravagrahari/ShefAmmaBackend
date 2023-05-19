package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GuestAccountImpl implements GuestAccount{

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Override
    public ResponseEntity<GuestAccountEntity> saveGuestSignup(GuestAccountEntity guestAccountEntity) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":email", new AttributeValue().withS(guestAccountEntity.getGuestEmail()));
        expressionAttributeValues.put(":phone", new AttributeValue().withS(guestAccountEntity.getGuestPhone()));

        DynamoDBQueryExpression<GuestAccountEntity> queryExpression = new DynamoDBQueryExpression<GuestAccountEntity>()
                .withKeyConditionExpression("email = :email and phone = :phone")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withProjectionExpression("email, phone");

        List<GuestAccountEntity> existingUsers = dynamoDBMapper.query(GuestAccountEntity.class, queryExpression);

        if (!existingUsers.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        dynamoDBMapper.save(guestAccountEntity);

        return ResponseEntity.ok(guestAccountEntity);
    }

    @Override
    public ResponseEntity<GuestAccountEntity>  getGuestLogin(GuestEntity guestentity) {
        return null;
    }
}
