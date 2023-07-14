package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.config.GuestAccountEntityUserDetails;
import com.shefamma.shefamma.entities.GuestAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
public class GuestAccountImpl implements GuestAccount,UserDetailsService{

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    String storedUuid;


    @Override
    public String saveGuestSignup(GuestAccountEntity guestAccountEntity) {
        guestAccountEntity.setPassword(passwordEncoder.encode(guestAccountEntity.getPassword()));
        dynamoDBMapper.save(guestAccountEntity);
        return  "guest#"+guestAccountEntity.getGuestUuid();
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        GuestAccountEntity guestAccount = findUserByPhone(phone);
        if (guestAccount == null) {
            throw new UsernameNotFoundException("user not found " + phone);
        }
        setStoredUuid(guestAccount.getGuestUuid());
        return new GuestAccountEntityUserDetails(guestAccount);
    }

    private GuestAccountEntity findUserByPhone(String phone) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":phone", new AttributeValue().withS(phone));

        DynamoDBQueryExpression<GuestAccountEntity> queryExpression = new DynamoDBQueryExpression<GuestAccountEntity>()
                .withKeyConditionExpression("pk = :phone")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(1);

        List<GuestAccountEntity> users = dynamoDBMapper.query(GuestAccountEntity.class, queryExpression);
        return users.isEmpty() ? null : users.get(0);
    }
    @Override
    public String storeGuestUuid() {
        return "guest#"+storedUuid;
    }

    public void setStoredUuid(String storedUuid) {
        this.storedUuid = storedUuid;
    }

}
//    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
//        expressionAttributeValues.put(":email", new AttributeValue().withS(guestAccountEntity.getGuestEmail()));
//                expressionAttributeValues.put(":phone", new AttributeValue().withS(guestAccountEntity.getGuestPhone()));
//
//                DynamoDBQueryExpression<GuestAccountEntity> queryExpression = new DynamoDBQueryExpression<GuestAccountEntity>()
//        .withKeyConditionExpression("email = :email and phone = :phone")
//        .withExpressionAttributeValues(expressionAttributeValues)
//        .withProjectionExpression("email, phone");
//
//        List<GuestAccountEntity> existingUsers = dynamoDBMapper.query(GuestAccountEntity.class, queryExpression);
//
//        if (!existingUsers.isEmpty()) {
//        return ResponseEntity.badRequest().build();
//        }
//
//        dynamoDBMapper.save(guestAccountEntity);