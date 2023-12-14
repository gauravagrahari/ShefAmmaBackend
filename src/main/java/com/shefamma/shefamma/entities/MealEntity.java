package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class MealEntity {

//    public void setUuidMeal(String uuidMeal) {
//        if (uuidMeal.startsWith("item#")) {
//            this.uuidMeal = uuidMeal;
//        } else {
//            this.uuidMeal = "item#" + uuidMeal;
//        }
//    }
    public void setMealType(String mealType) {
        // Convert the nameItem to lowercase before setting the value
        this.mealType = mealType.toLowerCase();
    }

    @DynamoDBHashKey(attributeName = "pk")
    private String uuidMeal;
    @DynamoDBRangeKey(attributeName = "sk")
    private String nameItem;
    @DynamoDBAttribute(attributeName = "meal")
    private String mealType;//breakfast,lunch,dinner
    @DynamoDBAttribute(attributeName = "dp")
    private String dp;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
    @DynamoDBAttribute(attributeName = "dsec")
    private String description;
    @DynamoDBAttribute(attributeName = "veg")
    private String vegetarian;
    @DynamoDBAttribute(attributeName = "amnt")//earlier here was amnt, in db also there is amnt
    private String amount;

    //These are not mandatory fields for mealItems
    @DynamoDBAttribute(attributeName = "ser")
    private String serveQuantity;
    @DynamoDBAttribute(attributeName = "serTy")
    private String serveType;//volume, weight, peices, plate
    @DynamoDBAttribute(attributeName = "spIng")
    private String specialIngredient;
    @DynamoDBAttribute(attributeName = "gsk")
    private String dishcategory;
}
