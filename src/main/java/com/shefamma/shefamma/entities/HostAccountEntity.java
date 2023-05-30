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
public class HostAccountEntity {
    @DynamoDBHashKey(attributeName = "pk")
    private String hostPhone;
    @DynamoDBRangeKey(attributeName = "sk")
    private String hostEmail;
    private String timeStamp;
    @DynamoDBAttribute(attributeName = "pass")
    private String password;
    @DynamoDBAutoGeneratedKey
    @DynamoDBAttribute(attributeName = "uuid")
    private String hostUuid;

}
