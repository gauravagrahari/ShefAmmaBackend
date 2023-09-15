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

    public void setUuidGuest(String uuidGuest) {
        if (uuidGuest.startsWith("guest#")) {
            this.uuidGuest = uuidGuest;
        } else {
            this.uuidGuest = "guest#" + uuidGuest;
        }
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidGuest;
    @DynamoDBRangeKey(attributeName = "sk")
    private String geocode;
    @DynamoDBAttribute(attributeName = "geoOff")
    private String geocodeOffice;
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;
    @DynamoDBAttribute(attributeName = "adr")
    private AdressSubEntity addressGuest;
    @DynamoDBAttribute(attributeName = "adrOff")
//    @DynamoDBTypeConverted(converter = AdressConverter.Converter.class)
    private AdressSubEntity officeAddress;
    @DynamoDBAttribute(attributeName = "dob")
    private String dob;
    @DynamoDBAttribute(attributeName = "gen")
    private String gender;

//    public String getOfficeAddress() {
//        return officeAddress != null ? officeAddress.convertToString() : null;
//    }
}
//{
//        "uuidGuest": "guest#123456789",
//        "geocode": "geocode123",
//        "name": "John Doe",
//        "DP": "",
//        "addressGuest": {},
//        "dob": "1990-01-01",
//        "gender": "Male"
//        }
