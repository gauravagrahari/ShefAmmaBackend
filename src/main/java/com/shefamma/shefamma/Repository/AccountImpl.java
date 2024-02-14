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

//@Repository
public class AccountImpl implements Account, UserDetailsService{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommonMethods commonMethods;
    private ThreadLocal<String> storedUuid = new ThreadLocal<>();
    private ThreadLocal<String> storedTimestamp = new ThreadLocal<>();

    // Accessors and Mutators
    public String getStoredUuid() {
        return storedUuid.get();
    }

    public void setStoredUuid(String uuid) {
        storedUuid.set(uuid);
    }

    public String getStoredTimestamp() {
        return storedTimestamp.get();
    }

    public void setStoredTimestamp(String timestamp) {
        storedTimestamp.set(timestamp);
    }

    //    public void setStoredTimestamp(String storedTimestamp) {
//        this.storedTimestamp = storedTimestamp;
//    }
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
        // Check if the password matches
        return passwordEncoder.matches(oldPassword, accountEntity.getPassword());
    }
    @Override
    public String sendTimeStamp(String number){
        AccountEntity accountEntity = findUserByPhone(number);

        if (accountEntity == null) {
            throw new UsernameNotFoundException("User not found for uuid: " + number);
        }
        return accountEntity.getTimeStamp();
    }

    @Override
    public String storeHostUuid() {
        String currentUuid = getStoredUuid(); // Get the string value first
        if (currentUuid != null && !currentUuid.startsWith("host#")) {
            return "host#" + currentUuid;
        }
        return currentUuid;
    }
    @Override
    public String storeGuestUuid() {
        String currentUuid = getStoredUuid(); // Correctly access the String value
        if (currentUuid != null && !currentUuid.startsWith("guest#")) {
            currentUuid = "guest#" + currentUuid;
            setStoredUuid(currentUuid); // Update the storedUuid with the new value
        }
        return currentUuid;
    }

    @Override
    public String storeDevBoyUuid() {
        String currentUuid = getStoredUuid(); // Correctly access the String value
        if (currentUuid != null && !currentUuid.startsWith("devBoy#")) {
            currentUuid = "devBoy#" + currentUuid;
            setStoredUuid(currentUuid); // Update the storedUuid with the new value
        }
        return currentUuid;
    }

//    public void setStoredUuid(String storedUuid) {
//        this.storedUuid = storedUuid;
//    }
public String storeTimestamp() {
    return storedTimestamp.get(); // Correctly access and return the String value
}


    @Override
    public String storeAdminUuid() {
        String currentUuid = getStoredUuid(); // Correctly access the String value
        if (currentUuid != null && !currentUuid.startsWith("admin#")) {
            currentUuid = "admin#" + currentUuid;
            setStoredUuid(currentUuid); // Update the storedUuid with the new value
        }
        return currentUuid;
    }



}
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        HostAccountEntity hostAccountEntity = dynamoDBMapper.load(HostAccountEntity.class, email);
//        if (hostAccountEntity == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//        return new HostAccountUserDetails(hostAccountEntity);
//    }
//
//    public void saveHost(HostAccountEntity hostAccountEntity) {
//        hostAccountEntity.setPassword(passwordEncoder.encode(hostAccountEntity.getPassword()));
//        dynamoDBMapper.save(hostAccountEntity);
//    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }

//public ResponseEntity<HostAccountEntity> saveHostSignup(HostAccountEntity hostAccountEntity) {
//    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
////        expressionAttributeValues.put(":email", new AttributeValue().withS(hostAccountEntity.getHostEmail()));
//    expressionAttributeValues.put(":phone", new AttributeValue().withS(hostAccountEntity.getHostPhone()));
//    DynamoDBQueryExpression<HostAccountEntity> queryExpression = new DynamoDBQueryExpression<HostAccountEntity>()
////                .withKeyConditionExpression("email = :email and phone = :phone")
//            .withKeyConditionExpression("phone = :phone")
//            .withExpressionAttributeValues(expressionAttributeValues)
//            .withProjectionExpression("email, phone");
//    List<HostAccountEntity> existingUsers = dynamoDBMapper.query(HostAccountEntity.class, queryExpression);
//    if (!existingUsers.isEmpty()) {
//        return ResponseEntity.badRequest().build();
//    }
//
//    dynamoDBMapper.save(hostAccountEntity);
//
//    return ResponseEntity.ok(hostAccountEntity);
//}