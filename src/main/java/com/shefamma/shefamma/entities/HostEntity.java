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
    public void setUuidHost(String uuidHost) {
        if (uuidHost.startsWith("host#")) {
            this.uuidHost = uuidHost;
        } else {
            this.uuidHost = "host#" + uuidHost;
        }
    }

    public void setUuidHostGsi(String uuidHostGsi) {
        if (uuidHostGsi.startsWith("host#")) {
            this.uuidHostGsi = uuidHostGsi;
        } else {
            this.uuidHostGsi = "host#" + uuidHostGsi;
        }
    }

    public void setGsiPk(String gsiPk) {
        this.gsiPk = "h";
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidHost;
    @DynamoDBRangeKey(attributeName = "sk")
    private String geocode;
    @DynamoDBIndexHashKey(attributeName = "gpk",globalSecondaryIndexName = "gsi1")
    private String gsiPk;
    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
    private String uuidHostGsi;
    @DynamoDBAttribute(attributeName = "dCat")
    private String dineCategory;
    @DynamoDBAttribute(attributeName = "DDP")
    private String DDP;
     @DynamoDBAttribute(attributeName = "name")
    private String nameHost;
    @DynamoDBAttribute(attributeName = "DP")
    private String DP;
    @DynamoDBAttribute(attributeName = "dsec")
    private String descriptionHost;
    @DynamoDBAttribute(attributeName = "curMes")
    private String currentMessage;
    @DynamoDBAttribute(attributeName = "adr")
    private AdressSubEntity addressHost;
    @DynamoDBAttribute(attributeName = "ratH")
    private String ratingHost;
    @DynamoDBAttribute(attributeName = "noOfRat")
    private String noOfRating;
}
