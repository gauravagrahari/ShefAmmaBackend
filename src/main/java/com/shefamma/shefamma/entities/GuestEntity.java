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
public class GuestEntity {
    public void setUuidGuest(String uuidGuest) {
        this.uuidGuest = uuidGuest+"guest";
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidGuest;//h_id
    @DynamoDBRangeKey(attributeName = "sk")
    private String geocode;//hName
    @DynamoDBAttribute(attributeName = "name")
    private String name;//hEmail
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;//hDP
    @DynamoDBAttribute(attributeName = "adr")
    private AdressSubEntity addressGuest;//hAdrress
    @DynamoDBAttribute(attributeName = "dob")
    private String dob;//hDP
@DynamoDBAttribute(attributeName = "gen")
    private String gender;//hDP
}
