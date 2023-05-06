package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class AdressSubEntity {
    @DynamoDBAttribute
    private String street;
    @DynamoDBAttribute
    private String houseName;
    @DynamoDBAttribute
    private String city;
    @DynamoDBAttribute
    private String state;
    @DynamoDBAttribute
    private String pinCode;

}
