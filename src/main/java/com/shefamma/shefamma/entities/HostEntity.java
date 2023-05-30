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
        this.uuidHost = uuidHost+"#host";
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidHost;
    @DynamoDBRangeKey(attributeName = "sk")
    @DynamoDBIndexHashKey
    private String geocode;//hName
    @DynamoDBIndexRangeKey(attributeName = "gsk")
    private String dineCategory;//hEmail
    @DynamoDBAttribute(attributeName = "DDP")
    private String DDP;//DDP
     @DynamoDBAttribute(attributeName = "name")
    private String nameHost;//DDP

    @DynamoDBAttribute(attributeName = "DP")
    private String DP;//hDP
    @DynamoDBAttribute(attributeName = "dsec")
    private String descriptionHost;//hDP
    @DynamoDBAttribute(attributeName = "curMes")
    private String currentMessage;//hDP

    @DynamoDBAttribute(attributeName = "adr")
    private AdressSubEntity addressHost;//hAdrress

}
