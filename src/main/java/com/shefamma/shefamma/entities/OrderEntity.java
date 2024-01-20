package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.List;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmmaProd")
public class  OrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public OrderEntity(String order1, String geo11, String geo12, String lunch) {
    }

    public void setUuidItem(String uuidOrder) {
        if (uuidOrder.startsWith("order#")) {
            this.uuidOrder = uuidOrder;
        } else {
            this.uuidOrder = "order#" + uuidOrder;
        }
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidOrder;//uuid will be of guest
    @DynamoDBRangeKey(attributeName = "sk")
    private String timeStamp;
    @DynamoDBIndexHashKey(attributeName = "gpk", globalSecondaryIndexName = "gsi1")
    private String uuidHost;
    @DynamoDBIndexRangeKey(attributeName = "gsk",globalSecondaryIndexName = "gsi1")
    private String timeStampGsi;
      @DynamoDBIndexHashKey(attributeName = "gpk2", globalSecondaryIndexName = "gsi2")
    private String uuidDevBoy;
    @DynamoDBIndexRangeKey(attributeName = "gsk2",globalSecondaryIndexName = "gsi2")
    private String timeStampGsiDev;
    @DynamoDBAttribute(attributeName = "nameG")
    private String nameGuest;
     @DynamoDBAttribute(attributeName = "nameH")
    private String nameHost;
    @DynamoDBAttribute(attributeName = "phoneG")
    private String phoneGuest;
    @DynamoDBAttribute(attributeName = "phoneGAlt")
    private String phoneGuestAlt;
    @DynamoDBAttribute(attributeName = "phoneHAlt")
    private String phoneHostAlt;
    @DynamoDBAttribute(attributeName = "phoneH")
    private String phoneHost;
    @DynamoDBAttribute(attributeName = "geo1")
    private String geoGuest;
    @DynamoDBAttribute(attributeName = "geo2")
    private String geoHost;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
    @DynamoDBAttribute(attributeName = "amtTot")
    private String amount;

    @DynamoDBAttribute(attributeName = "rat")
    private String rating;
    @DynamoDBAttribute(attributeName = "pTime")
    private String pickUpTime;
    @DynamoDBAttribute(attributeName = "dTime")
    private String deliverTime;
    @DynamoDBAttribute(attributeName = "cTime")
    private String cancelledTime;
    @DynamoDBAttribute(attributeName="rev")
    private String review;

    @DynamoDBAttribute(attributeName = "pyMd")
    private String payMode;
    @DynamoDBAttribute(attributeName = "delTD")
    private String delTimeAndDay;
    @DynamoDBAttribute(attributeName = "delAdd")
    private AdressSubEntity delAddress;
    @DynamoDBAttribute(attributeName = "meal")
    private String mealType;//b, d, l

    @DynamoDBAttribute(attributeName="name")
    private String itemName;
    @DynamoDBAttribute(attributeName="amt")
    private String itemPrice;
    @DynamoDBAttribute(attributeName="nOS")
    private String noOfServing;
    @DynamoDBAttribute(attributeName="prefTime")
    private String preferredTime;

    @DynamoDBAttribute(attributeName="amtCook")
    private String amtCook;

    @DynamoDBAttribute(attributeName="amtDel")
    private String amtDelivery;
    @DynamoDBAttribute(attributeName="amtPack")
    private String amtPackaging;


    public String addressToString(AdressSubEntity address) {
        return String.format("%s, %s, %s, %s, %s",
                address.getHouseName(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPinCode());
    }
}