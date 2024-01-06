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
@DynamoDBTable(tableName = "ShefAmmaProd")
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
    private AdressSubEntity locationDevBoy;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
    @DynamoDBAttribute(attributeName ="veh")
    private String vehicleType;
}
