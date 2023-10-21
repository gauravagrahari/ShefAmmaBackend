package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class PincodeEntity {


    public PincodeEntity setPincode(String pincode) {
        if(pincode.startsWith("pin#")){
        this.pincode = pincode;
        }else{
            this.pincode ="pin#" + pincode;
        }
        return this;
    }

    @DynamoDBHashKey(attributeName = "pk")
    private String pincode;
    @DynamoDBRangeKey(attributeName = "sk")
    private String sk="pin";


}
