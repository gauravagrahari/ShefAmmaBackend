package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.shefamma.shefamma.config.AccountEntityUserDetails;
import com.shefamma.shefamma.entities.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccountImpl implements Account, UserDetailsService{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommonMethods commonMethods;
    String storedUuid;
    private String storedTimestamp;

    @Override
    public AccountEntity saveSignup(AccountEntity accountEntity,String user) {
        accountEntity.setPassword(passwordEncoder.encode(accountEntity.getPassword()));
        dynamoDBMapper.save(accountEntity);
        setStoredUuid(user+"#"+accountEntity.getUuid());
        setStoredTimestamp(accountEntity.getTimeStamp());
        return accountEntity;
    }


    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        AccountEntity hostAccount = findUserByPhone(phone);
        if (hostAccount == null) {
            throw new UsernameNotFoundException("User not found " + phone);
        }
        setStoredUuid(hostAccount.getUuid());
        setStoredTimestamp(hostAccount.getTimeStamp());

        return new AccountEntityUserDetails(hostAccount);
    }

    private AccountEntity findUserByPhone(String phone) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":phone", new AttributeValue().withS(phone));

        DynamoDBQueryExpression<AccountEntity> queryExpression = new DynamoDBQueryExpression<AccountEntity>()
                .withKeyConditionExpression("pk = :phone")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(1);

        List<AccountEntity> users = dynamoDBMapper.query(AccountEntity.class, queryExpression);
        return users.isEmpty() ? null : users.get(0);
    }
    @Override
    public AccountEntity findUserByEmail(String email) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":email", new AttributeValue().withS(email));

        DynamoDBQueryExpression<AccountEntity> queryExpression = new DynamoDBQueryExpression<AccountEntity>()
                .withIndexName("gsi1")
                .withConsistentRead(false)
                .withKeyConditionExpression("gpk = :email")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(1);

        List<AccountEntity> users = dynamoDBMapper.query(AccountEntity.class, queryExpression);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public ResponseEntity<?> changePassword(String phone, String timeStamp, String newPassword) {
        AccountEntity accountEntity = findUserByPhone(phone);

        if (accountEntity == null) {
            String errorMessage = "User not found for phone: " + phone;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        UpdateItemResult result = commonMethods.updateAttributeWithSortKey(phone, timeStamp, "pass", passwordEncoder.encode(newPassword));

        if (result != null && result.getAttributes() != null && !result.getAttributes().isEmpty()) {
            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password.");
        }
    }


    @Override
    public boolean isPasswordCorrect(String uuidHost, String oldPassword) {
        AccountEntity accountEntity = findUserByPhone(uuidHost);

        if (accountEntity == null) {
            throw new UsernameNotFoundException("User not found for uuid: " + uuidHost);
        }

        
        return passwordEncoder.matches(oldPassword, accountEntity.getPassword());
    }
    @Override
    public String storeHostUuid() {
        if (storedUuid != null && !storedUuid.startsWith("host#")) {
            return "host#" + storedUuid;
        }
        return storedUuid;
    }
    @Override
    public String storeGuestUuid() {
        if (storedUuid != null && !storedUuid.startsWith("guest#")) {
            return "guest#" + storedUuid;
        }
        return storedUuid;
    }
    @Override
    public String storeDevBoyUuid() {
        if (storedUuid != null && !storedUuid.startsWith("devBoy#")) {
            return "devBoy#" + storedUuid;
        }
        return storedUuid;
    }

    public void setStoredUuid(String storedUuid) {
        this.storedUuid = storedUuid;
    }
    public String storeTimestamp() {
        return storedTimestamp;
    }

    @Override
    public String storeAdminUuid() {
        if (storedUuid != null && !storedUuid.startsWith("admin#")) {
            return "admin#" + storedUuid;
        }
        return storedUuid;
    }

    public void setStoredTimestamp(String storedTimestamp) {
        this.storedTimestamp = storedTimestamp;
    }
}




































