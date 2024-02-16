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
public class ConstantChargesEntity {

    // Primary key for the entity (you can customize as needed)
    @DynamoDBHashKey(attributeName = "pk")
    private String constantCharges = "constantCharges"; // You can choose any suitable value for the primary key

    @DynamoDBRangeKey(attributeName = "sk")
    private String sk = "default"; // You can choose any suitable value for the range key

    // Variables for constant charges and discounts
    @DynamoDBAttribute(attributeName = "delCh")
    private String deliveryCharges;

    @DynamoDBAttribute(attributeName = "packCh")
    private String packagingCharges;

    @DynamoDBAttribute(attributeName = "hanCh")
    private String handlingCharges;

    @DynamoDBAttribute(attributeName = "discount")
    private String discount;

    @DynamoDBAttribute(attributeName = "BreakfastStartTime")
    private String breakfastStartTime;
    @DynamoDBAttribute(attributeName = "LunchStartTime")
    private String lunchStartTime;
    @DynamoDBAttribute(attributeName = "DinnerStartTime")
    private String dinnerStartTime;
    @DynamoDBAttribute(attributeName = "BreakfastEndTime")
    private String breakfastEndTime;
    @DynamoDBAttribute(attributeName = "LunchEndTime")
    private String lunchEndTime;
    @DynamoDBAttribute(attributeName = "DinnerEndTime")
    private String dinnerEndTime;
    @DynamoDBAttribute(attributeName = "BreakfastBookTime")
    private String breakfastBookTime;
    @DynamoDBAttribute(attributeName = "LunchBookTime")
    private String lunchBookTime;
    @DynamoDBAttribute(attributeName = "DinnerBookTime")
    private String dinnerBookTime;
    @DynamoDBAttribute(attributeName = "cancelCutOffTime")
    private String cancelCutOffTime;
    @DynamoDBAttribute(attributeName = "maxMeal")
    private String maxMeal;
    @DynamoDBAttribute(attributeName = "eHanCh")
    private String extraHandlingCharges;
    @DynamoDBAttribute(attributeName = "cutleryCh")
    private String cutleryCharge;
}
