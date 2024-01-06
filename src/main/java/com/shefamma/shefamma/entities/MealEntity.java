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
@DynamoDBTable(tableName = "ShefAmmaProd")
public class MealEntity {








    public void setMealType(String mealType) {
        
        this.mealType = mealType.toLowerCase();
    }

    @DynamoDBHashKey(attributeName = "pk")
    private String uuidMeal;
    @DynamoDBRangeKey(attributeName = "sk")
    private String nameItem;
    @DynamoDBAttribute(attributeName = "meal")
    private String mealType;
    @DynamoDBAttribute(attributeName = "dp")
    private String dp;
    @DynamoDBAttribute(attributeName = "stts")
    private String status;
    @DynamoDBAttribute(attributeName = "dsec")
    private String description;
    @DynamoDBAttribute(attributeName = "veg")
    private String vegetarian;
    @DynamoDBAttribute(attributeName = "amnt")
    private String amount;

    
    @DynamoDBAttribute(attributeName = "ser")
    private String serveQuantity;
    @DynamoDBAttribute(attributeName = "serTy")
    private String serveType;
    @DynamoDBAttribute(attributeName = "spIng")
    private String specialIngredient;
    @DynamoDBAttribute(attributeName = "gsk")
    private String dishcategory;
}
