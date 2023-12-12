package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class PincodeEntity {
    @DynamoDBHashKey(attributeName = "pk")
    private String pk = "pin";

    @DynamoDBRangeKey(attributeName = "sk")
    private String pincode;

    // Getter and setter for status
    @Getter
    @DynamoDBAttribute(attributeName = "status")
    private boolean status; // New attribute for active/inactive status

    public PincodeEntity setPincode(String pincode) {
        this.pincode = pincode;
        return this;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setPk(String pk) {
        this.pk = pk;
    }

}
