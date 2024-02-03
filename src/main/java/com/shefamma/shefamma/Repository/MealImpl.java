package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.HostEntity;
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
    @Autowired
    HostEntity hostentity;
    @Override
    public ResponseEntity<MealEntity> createMeal(MealEntity mealEntity) {
        try {
            dynamoDBMapper.save(mealEntity);
            return new ResponseEntity<>(mealEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the error and return an appropriate error response
            // For example, you might return a 500 Internal Server Error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public MealEntity updateMealAttribute(String partition, String sort, String attributeName, MealEntity itemEntity) {
        String value = null;
        switch (attributeName) {
            case "itemNames":
                value = itemEntity.getNameItem();
                attributeName = "sk";
                break;
            case "dishcategory":
                value = itemEntity.getDishcategory();
                attributeName = "provMeals";
                break;
            case "dp" :
                value = itemEntity.getDp();
                attributeName = "dp";
                break;
            case "status":
                value = itemEntity.getStatus();
                attributeName = "stts";
                break;
            case "description":
                value = itemEntity.getDescription();
                attributeName = "dsec";
                break;
            case "vegetarian":
                value = itemEntity.getVegetarian();
                attributeName = "veg";
                break;
            case "amount":
                value = itemEntity.getAmount();
                attributeName = "amnt";
                break;
            // Add more cases for other attributes if needed
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        commonMethods.updateAttributeWithSortKey(partition,sort,attributeName,value);
        return itemEntity;
    }
    @Override
    public List<MealEntity> getItems(String mealId) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pk", new AttributeValue().withS(mealId));

        DynamoDBQueryExpression<MealEntity> queryExpression = new DynamoDBQueryExpression<MealEntity>()
                .withKeyConditionExpression("pk = :pk") //we need to use the attribute available in the dynamodbtable
                .withExpressionAttributeValues(expressionAttributeValues);
        return dynamoDBMapper.query(MealEntity.class, queryExpression);
    }
    @Override
    public MealEntity getItem(String hostId, String nameItem, MealEntity itementity) {
        return dynamoDBMapper.load(MealEntity.class, hostId, nameItem);
    }
    @Override
    public MealEntity getOneMeal(String mealId, String nameItem) {
        return dynamoDBMapper.load(MealEntity.class, mealId, nameItem);
    }
}
