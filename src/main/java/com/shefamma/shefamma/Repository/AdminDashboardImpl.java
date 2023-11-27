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

        // Generate JWT token
        String token = jwtServices.generateToken(adminDashboardEntity.getId());

        return ResponseEntity.status(HttpStatus.OK).body(token); // Return the token to the client
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

        // Check if the password matches
        return passwordEncoder.matches(oldPassword, accountEntity.getPassword());
    }
    @Override
    public ResponseEntity<String> saveSignup(AdminDashboardEntity adminDashboardEntity) {
        // Check if the user with the provided ID (or another unique attribute) already exists
        AdminDashboardEntity existingAdmin = findUserById(adminDashboardEntity.getId());
        if (existingAdmin != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An admin with the given ID already exists.");
        }
        // Encode the password
        adminDashboardEntity.setPassword(passwordEncoder.encode(adminDashboardEntity.getPassword()));
        // Save the new admin entity to DynamoDB
        dynamoDBMapper.save(adminDashboardEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful");
    }

    @Override
    public List<DevBoyEntity> getAllDevBoys() {
        try {
            // Define expression attribute values for querying the GSI
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":gpk", new AttributeValue().withS("d")); // Assuming 'h' is the common value for all hosts in 'gpk'
            eav.put(":gsk", new AttributeValue().withS("devBoy#"));

            // Define the query expression for the GSI
            DynamoDBQueryExpression<DevBoyEntity> queryExpression = new DynamoDBQueryExpression<DevBoyEntity>()
                    .withIndexName("gsi1")
                    .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                    .withExpressionAttributeValues(eav)
                    .withConsistentRead(false);

            return dynamoDBMapper.query(DevBoyEntity.class, queryExpression);

        } catch (Exception e) {
            // Log the exception and handle it as per your application's error handling policy
            logger.error("Error fetching all hosts from DynamoDB", e);
            throw new RuntimeException("Error fetching all hosts from DynamoDB", e);
        }
    }
    @Override
    public List<DevBoyEntity> getAllDevBoysIds() {
        try {
            // Define expression attribute values for querying the GSI
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":gpk", new AttributeValue().withS("d")); // Assuming 'h' is the common value for all hosts in 'gpk'
            eav.put(":gsk", new AttributeValue().withS("devBoy#"));

            // Define the query expression for the GSI
            DynamoDBQueryExpression<DevBoyEntity> queryExpression = new DynamoDBQueryExpression<DevBoyEntity>()
                    .withIndexName("gsi1")
                    .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                    .withExpressionAttributeValues(eav)
                    .withConsistentRead(false)
                    .withProjectionExpression("gsk");;

            return dynamoDBMapper.query(DevBoyEntity.class, queryExpression);

        } catch (Exception e) {
            // Log the exception and handle it as per your application's error handling policy
            logger.error("Error fetching all hosts from DynamoDB", e);
            throw new RuntimeException("Error fetching all hosts from DynamoDB", e);
        }
    }
    @Override
    public List<HostEntity>  getAllHosts() {
        try {
            // Define expression attribute values for querying the GSI
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":gpk", new AttributeValue().withS("h")); // Assuming 'h' is the common value for all hosts in 'gpk'
            eav.put(":gsk", new AttributeValue().withS("host#"));

            // Define the query expression for the GSI
            DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                    .withIndexName("gsi1")
                    .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                    .withExpressionAttributeValues(eav)
                    .withConsistentRead(false);

            return dynamoDBMapper.query(HostEntity.class, queryExpression);

        } catch (Exception e) {
            // Log the exception and handle it as per your application's error handling policy
            logger.error("Error fetching all hosts from DynamoDB", e);
            throw new RuntimeException("Error fetching all hosts from DynamoDB", e);
        }
    }
    @Override
    public List<HostEntity>  getAllHostsIds() {
        try {
            // Define expression attribute values for querying the GSI
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":gpk", new AttributeValue().withS("h")); // Assuming 'h' is the common value for all hosts in 'gpk'
            eav.put(":gsk", new AttributeValue().withS("host#"));

            // Define the query expression for the GSI
            DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                    .withIndexName("gsi1")
                    .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                    .withExpressionAttributeValues(eav)
                    .withConsistentRead(false)
                    .withProjectionExpression("gsk");

            return dynamoDBMapper.query(HostEntity.class, queryExpression);

        } catch (Exception e) {
            // Log the exception and handle it as per your application's error handling policy
            logger.error("Error fetching all hosts from DynamoDB", e);
            throw new RuntimeException("Error fetching all hosts from DynamoDB", e);
        }
    }

}
