package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.AdminDashboardEntity;
import com.shefamma.shefamma.entities.DevBoyEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.services.JwtServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminDashboardImpl implements AdminDashboard{
    @Autowired
    private JwtServices jwtServices;
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public ResponseEntity<String> login(AdminDashboardEntity adminDashboardEntity) {
        boolean isPasswordCorrect = isPasswordCorrect(adminDashboardEntity.getId(), adminDashboardEntity.getPassword());

        if (!isPasswordCorrect) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials for id: " + adminDashboardEntity.getId());
        }

        
        String token = jwtServices.generateToken(adminDashboardEntity.getId());

        return ResponseEntity.status(HttpStatus.OK).body(token); 
    }

    private AdminDashboardEntity findUserById(String id) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":id", new AttributeValue().withS(id));

        DynamoDBQueryExpression<AdminDashboardEntity> queryExpression = new DynamoDBQueryExpression<AdminDashboardEntity>()
                .withKeyConditionExpression("pk = :id")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(1);

        List<AdminDashboardEntity> users = dynamoDBMapper.query(AdminDashboardEntity.class, queryExpression);
        return users.isEmpty() ? null : users.get(0);
    }
    @Override
    public boolean isPasswordCorrect(String id, String oldPassword) {
        AdminDashboardEntity accountEntity = findUserById(id);

        if (accountEntity == null) {
            throw new UsernameNotFoundException("User not found for id: " + id);
        }

        
        return passwordEncoder.matches(oldPassword, accountEntity.getPassword());
    }
    @Override
    public ResponseEntity<String> saveSignup(AdminDashboardEntity adminDashboardEntity) {
        
        AdminDashboardEntity existingAdmin = findUserById(adminDashboardEntity.getId());
        if (existingAdmin != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An admin with the given ID already exists.");
        }
        
        adminDashboardEntity.setPassword(passwordEncoder.encode(adminDashboardEntity.getPassword()));
        
        dynamoDBMapper.save(adminDashboardEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful");
    }
    @Override
    public List<DevBoyEntity> getAllDevBoys() {
        return queryDynamoDB(DevBoyEntity.class, "gsi1", "d", "devBoy#", null);
    }

    @Override
    public List<DevBoyEntity> getAllDevBoysIds() {
        return queryDynamoDB(DevBoyEntity.class, "gsi1", "d", "devBoy#", "gsk");
    }

    @Override
    public List<HostEntity> getAllHosts() {
        return queryDynamoDB(HostEntity.class, "gsi1", "h", "host#", null);
    }

    @Override
    public List<HostEntity> getAllHostsIds() {
        return queryDynamoDB(HostEntity.class, "gsi1", "h", "host#", "gsk");
    }
    private <T> List<T> queryDynamoDB(Class<T> clazz, String gsiName, String partitionKey, String sortKeyPrefix, String projectionExpression) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":gpk", new AttributeValue().withS(partitionKey));
            eav.put(":gsk", new AttributeValue().withS(sortKeyPrefix));

            DynamoDBQueryExpression<T> queryExpression = new DynamoDBQueryExpression<T>()
                    .withIndexName(gsiName)
                    .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                    .withExpressionAttributeValues(eav)
                    .withConsistentRead(false);

            if (projectionExpression != null) {
                queryExpression.withProjectionExpression(projectionExpression);
            }

            return dynamoDBMapper.query(clazz, queryExpression);
        } catch (Exception e) {
            logger.error("Error fetching data from DynamoDB for " + clazz.getSimpleName(), e);
            throw new RuntimeException("Error fetching data from DynamoDB for " + clazz.getSimpleName(), e);
        }
    }

}
