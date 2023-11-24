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
public class DevBoyEntity {
    public void setUuidDevBoy(String uuidDevBoy) {
        if (uuidDevBoy.startsWith("devBoy#")) {
            this.uuidDevBoy = uuidDevBoy;
        } else{
            this.uuidDevBoy ="devBoy#"+ uuidDevBoy;
        }
    }

    public void setGsiPk(String gsiPk) {
        this.gsiPk = "d";
    }
    public void setGsiSk(String gsiSk) {
        if (gsiSk.startsWith("devBoy#")) {
            this.gsiSk = gsiSk;
        } else {
            this.gsiSk = "devBoy#" + gsiSk;
        }
    }
    @DynamoDBIndexHashKey(attributeName = "gpk",globalSecondaryIndexName = "gsi1")
    private String gsiPk;
    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
    private String gsiSk;
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidDevBoy;
    @DynamoDBRangeKey(attributeName = "sk")
    private String geocode;
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;
    @DynamoDBAttribute(attributeName = "adr")
    private AdressSubEntity locationDevBoy;//this might be about the current location of the devboy or maybe the initial location
    @DynamoDBAttribute(attributeName = "stts")
    private String status;//Occupied, Available, OnLeave
    @DynamoDBAttribute(attributeName ="veh")
    private String vehicleType;
}
