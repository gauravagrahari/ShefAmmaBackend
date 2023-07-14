package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.config.AccountEntityUserDetails;
import com.shefamma.shefamma.entities.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
public class AccountImpl implements Account, UserDetailsService {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    String storedUuid;

    @Override
    public String saveSignup(AccountEntity accountEntity,String user) {
        accountEntity.setPassword(passwordEncoder.encode(accountEntity.getPassword()));
        dynamoDBMapper.save(accountEntity);
        return user+"#"+accountEntity.getUuid();
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        AccountEntity hostAccount = findUserByPhone(phone);
        if (hostAccount == null) {
            throw new UsernameNotFoundException("user not found " + phone);
        }
        setStoredUuid(hostAccount.getUuid());
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
    public String storeHostUuid() {
        return "host#"+storedUuid;
    }
    @Override
    public String storeGuestUuid() {
        return "guest#"+storedUuid;
    }

    public void setStoredUuid(String storedUuid) {
        this.storedUuid = storedUuid;
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