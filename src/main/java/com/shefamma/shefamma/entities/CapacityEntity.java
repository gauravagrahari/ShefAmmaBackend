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
public class CapacityEntity {
    public void setCapacity(String uuidCapacity){
        if(uuidCapacity.startsWith("capacity#")){
            this.uuidCapacity=uuidCapacity;
        }else{
            this.uuidCapacity="capacity#"+uuidCapacity;
        }
    }
    @DynamoDBHashKey(attributeName = "pk")
    private String uuidCapacity;//uuid will be of guest
    @DynamoDBRangeKey(attributeName = "sk")
    private String sk="capacity";

    @DynamoDBAttribute(attributeName = "bCap")
    private String breakfastCapacity;
    @DynamoDBAttribute(attributeName = "curBCap")
    private String currentBreakfastCapacity;
    @DynamoDBAttribute(attributeName = "lCap")
    private String lunchCapacity;
    @DynamoDBAttribute(attributeName = "curLCap")
    private String currentLunchCapacity;
    @DynamoDBAttribute(attributeName = "dCap")
    private String dinnerCapacity;
    @DynamoDBAttribute(attributeName = "curDCap")
    private String currentDinnerCapacity;


}
