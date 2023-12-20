package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.shefamma.shefamma.converter.AdressConverter;
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

    public GuestEntity setUuidGuest(String uuidGuest) {
        if (uuidGuest.startsWith("guest#")) {
            this.uuidGuest = uuidGuest;
        } else {
            this.uuidGuest = "guest#" + uuidGuest;
        }
        return this;
    }

    @DynamoDBHashKey(attributeName = "pk")
    private String uuidGuest;
    @DynamoDBRangeKey(attributeName = "sk")
    private String geocode;
    @DynamoDBAttribute(attributeName = "geoOff")
    private String geocodeOffice;
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBAttribute(attributeName = "dp")
    private String DP;
    @DynamoDBAttribute(attributeName = "adr")
    private AdressSubEntity addressGuest;
    @DynamoDBAttribute(attributeName = "adrOff")
    private AdressSubEntity officeAddress;
    @DynamoDBAttribute(attributeName = "dob")
    private String dob;
    @DynamoDBAttribute(attributeName = "gen")
    private String gender;
    @DynamoDBAttribute(attributeName = "phone")
    private String phone;
    @DynamoDBAttribute(attributeName = "phoneA")
    private String alternateMobile;

}









