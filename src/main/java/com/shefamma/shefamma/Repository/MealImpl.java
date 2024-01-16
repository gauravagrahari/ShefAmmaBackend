package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.MealEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MealImpl implements  Meal{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private CommonMethods commonMethods;

    @Override
    public ResponseEntity<MealEntity> createMeal(MealEntity mealEntity) {
        try {
            dynamoDBMapper.save(mealEntity);
            return new ResponseEntity<>(mealEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            
            
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public MealEntity updateMealAttribute(String partition, String sort, String attributeName, MealEntity itemEntity) {
        String value;
        switch (attributeName) {
            case "itemNames" -> {
                value = itemEntity.getNameItem();
                attributeName = "sk";
            }
            case "dishcategory" -> {
                value = itemEntity.getDishcategory();
                attributeName = "provMeals";
            }

            case "dp" ->{
                value = itemEntity.getDp();
                attributeName = "dp";
            }
            case "status" -> {
                value = itemEntity.getStatus();
                attributeName = "stts";
            }
            case "description" -> {
                value = itemEntity.getDescription();
                attributeName = "dsec";
            }
            case "vegetarian" -> {
                value = itemEntity.getVegetarian();
                attributeName = "veg";
            }
            case "amount" -> {
                value = itemEntity.getAmount();
                attributeName = "amnt";
            }
            default -> throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        commonMethods.updateAttributeWithSortKey(partition,sort,attributeName,value);
        return itemEntity;
    }
    @Override
    public List<MealEntity> getItems(String mealId) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pk", new AttributeValue().withS(mealId));

        DynamoDBQueryExpression<MealEntity> queryExpression = new DynamoDBQueryExpression<MealEntity>()
                .withKeyConditionExpression("pk = :pk") 
                .withExpressionAttributeValues(expressionAttributeValues);
        return dynamoDBMapper.query(MealEntity.class, queryExpression);
    }
    @Override
    public MealEntity getItem(String hostId, String nameItem, MealEntity itementity) {
        return dynamoDBMapper.load(MealEntity.class, hostId, nameItem);
    }
}
