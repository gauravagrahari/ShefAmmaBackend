package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data 
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class AccountEntity {
    @DynamoDBHashKey(attributeName = "pk")
    private String phone;
    @DynamoDBRangeKey(attributeName = "sk")
    private String timeStamp;
    @DynamoDBIndexHashKey(attributeName = "gpk",globalSecondaryIndexName = "gsi1")
    private String email;
    @DynamoDBAttribute(attributeName = "pass")
    private String password;
    @DynamoDBAutoGeneratedKey
    @DynamoDBAttribute(attributeName = "uuid")
    private String uuid;
    @DynamoDBAttribute(attributeName = "role")
    private String role;
}
//    @DynamoDBIndexHashKey(attributeName = "gpk2",globalSecondaryIndexName = "gsi2")
//    @DynamoDBRangeKey(attributeName = "sk")
//    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi")