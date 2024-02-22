package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmmaProd")
public class ServiceAvailability {
    @DynamoDBHashKey(attributeName = "pk")
    private String serviceDetails = "serviceDetails";

    @DynamoDBRangeKey(attributeName = "sk")
    private String sk = "default";
    @DynamoDBAttribute(attributeName = "message")
    private String serviceMessage;

}
