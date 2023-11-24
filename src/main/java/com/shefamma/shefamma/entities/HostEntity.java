package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class HostEntity {
    public HostEntity setUuidHost(String uuidHost) {
        if (uuidHost.startsWith("host#")) {
            this.uuidHost = uuidHost;
        } else {
            this.uuidHost = "host#" + uuidHost;
        }
        return this;
    }

    public void setGsiSk(String gsiSk) { //need to add gsiSk
        if (gsiSk.startsWith("host#")) {
            this.gsiSk = gsiSk;
        } else {
            this.gsiSk = "host#" + gsiSk;
        }
    }

    public void setGsiPk(String gsiPk) {
        this.gsiPk = "h";
    }
    @DynamoDBAttribute(attributeName = "adr")
    private AdressSubEntity addressHost;
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidHost;
    @DynamoDBRangeKey(attributeName = "sk")
    private String geocode;
    @DynamoDBIndexHashKey(attributeName = "gpk",globalSecondaryIndexName = "gsi1")
    private String gsiPk;
    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
    private String gsiSk;
    @DynamoDBAttribute(attributeName = "dCat")
    private String dineCategory;
    @DynamoDBAttribute(attributeName = "DDP")
    private String DDP;
    @DynamoDBAttribute(attributeName = "phone")
    private String phone;
     @DynamoDBAttribute(attributeName = "name")
    private String nameHost;
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;
    @DynamoDBAttribute(attributeName = "dsec")
    private String descriptionHost;
    @DynamoDBAttribute(attributeName = "curMes")
    private String currentMessage;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
//    @DynamoDBAttribute(attributeName = "bCap")
//    private String breakfastCapacity;
//    @DynamoDBAttribute(attributeName = "curBCap")
//    private String currentBreakfastCapacity;
//    @DynamoDBAttribute(attributeName = "lCap")
//    private String lunchCapacity;
//    @DynamoDBAttribute(attributeName = "curLCap")
//    private String currentLunchCapacity;
//    @DynamoDBAttribute(attributeName = "dCap")
//    private String dinnerCapacity;
//    @DynamoDBAttribute(attributeName = "curDCap")
//    private String currentDinnerCapacity;

    @DynamoDBAttribute(attributeName = "ratH")
    private String ratingHost;
    @DynamoDBAttribute(attributeName = "noOfRat")
    private String noOfRating;
    @DynamoDBAttribute(attributeName = "provMeals")
    private String providedMeals;//bld meaning breakfast lunch dinner

}
