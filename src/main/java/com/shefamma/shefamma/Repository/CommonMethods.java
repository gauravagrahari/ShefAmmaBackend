package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CommonMethods {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    String tableName="ShefAmma";

    public void updateAttribute(String partitionKeyValue, String attributeName, String newValue) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(partitionKeyValue));

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":newValue", new AttributeValue(newValue));

            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(tableName)
                    .withKey(key)
                    .withUpdateExpression("SET " + attributeName + " = :newValue")
                    .withExpressionAttributeValues(expressionAttributeValues);

            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    public UpdateItemResult updateAttributeWithSortKey(String partitionKeyValue, String sortKeyValue, String attributeName, String newValue) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(partitionKeyValue));
            key.put("sk", new AttributeValue(sortKeyValue));

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":newValue", new AttributeValue(newValue));

            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(tableName)
                    .withKey(key)
                    .withUpdateExpression("SET " + attributeName + " = :newValue")
                    .withExpressionAttributeValues(expressionAttributeValues)
                     .withReturnValues(ReturnValue.ALL_NEW); 

            return amazonDynamoDB.updateItem(updateItemRequest);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage());
        }
    }
    public ResponseEntity<?> updateTwoAttributesWithSortKey(String pkValue, String skValue,
                                                            String attrName1, String newValue1,
                                                            String attrName2, String newValue2) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(pkValue));
            key.put("sk", new AttributeValue(skValue));

            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":val1", new AttributeValue(newValue1));
            eav.put(":val2", new AttributeValue(newValue2));

            String updateExpr = String.format("SET %s = :val1, %s = :val2", attrName1, attrName2);

            UpdateItemRequest req = new UpdateItemRequest()
                    .withTableName(tableName)
                    .withKey(key)
                    .withUpdateExpression(updateExpr)
                    .withExpressionAttributeValues(eav);

            amazonDynamoDB.updateItem(req);

            return ResponseEntity.ok("Update successful.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    public void updateTimeSlotCapOrderEntity(String primaryKey, String primaryValue, String attributeName, String nestedAttributeName, String uniqueAttribute, String uniqueValue, String newValue, String index) {

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(primaryKey, new AttributeValue(primaryValue));

        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#nestedAttributeName", nestedAttributeName);
        expressionAttributeNames.put("#uniqueAttribute", uniqueAttribute);

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newValue", new AttributeValue(newValue));
        expressionAttributeValues.put(":uniqueValue", new AttributeValue(uniqueValue));
        expressionAttributeValues.put(":emptyList", new AttributeValue().withL(Collections.emptyList()));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(key)
                .withUpdateExpression("SET " + attributeName + "[" + index + "].#nestedAttributeName = " + attributeName + "[" + index + "].#nestedAttributeName - :newValue")
                .withConditionExpression("attribute_exists(" + attributeName + "[" + index + "]) AND " + attributeName + "[" + index + "].#nestedAttributeName >= :newValue")
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues);

        try {
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (ConditionalCheckFailedException e) {
            throw new IllegalArgumentException("New capacity is greater than current capacity - " + newValue);
        }
    }

    public void updateTimeSlotCapOrderEntity(String primaryKey, String primaryValue, String attributeName, String nestedAttributeName, String newValue, String index) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(primaryKey, new AttributeValue(primaryValue));

        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#a", attributeName);
        expressionAttributeNames.put("#n", nestedAttributeName);

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v", new AttributeValue().withN(newValue));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(key)
                .withUpdateExpression("SET #a[" + index + "].#n = :v")
                .withConditionExpression("attribute_exists(#a[" + index + "]) AND #a[" + index + "].#n >= :v")
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues);
        try {
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid index value: " + index);
        }

    }
    public void updateMultipleAttributes(String partitionKeyValue, Map<String, String> attributeValues) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(partitionKeyValue));

            
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            
            StringBuilder updateExpression = new StringBuilder("SET ");

            int index = 0;
            
            for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
                String attributeName = entry.getKey();
                String newValue = entry.getValue();
                String placeholder = ":newValue" + index;

                
                expressionAttributeValues.put(placeholder, new AttributeValue(newValue));

                
                updateExpression.append(attributeName).append(" = ").append(placeholder);
                if (index != attributeValues.size() - 1) {
                    updateExpression.append(", ");
                }
                index++;
            }

            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(tableName)
                    .withKey(key)
                    .withUpdateExpression(updateExpression.toString())
                    .withExpressionAttributeValues(expressionAttributeValues);

            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    public void updateMultipleAttributes(String partitionKeyValue, String sortKeyValue, Map<String, String> attributeValues) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(partitionKeyValue));
            key.put("sk", new AttributeValue(sortKeyValue));

            
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            
            StringBuilder updateExpression = new StringBuilder("SET ");

            int index = 0;
            
            for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
                String attributeName = entry.getKey();
                String newValue = entry.getValue();
                String placeholder = ":newValue" + index;

                
                expressionAttributeValues.put(placeholder, new AttributeValue(newValue));

                
                updateExpression.append(attributeName).append(" = ").append(placeholder);
                if (index != attributeValues.size() - 1) {
                    updateExpression.append(", ");
                }

                index++;
            }

            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(tableName)
                    .withKey(key)
                    .withUpdateExpression(updateExpression.toString())
                    .withExpressionAttributeValues(expressionAttributeValues);

            
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    public void updateSpecificNestedAttribute(String primaryKey, String primaryValue, String attributeName, String nestedAttributeName, String uniqueAttribute, String uniqueValue, String newValue) {
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(primaryKey, new AttributeValue(primaryValue));

        
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#index", "list_append(" + attributeName + ", :emptyList)[0]");
        expressionAttributeNames.put("#nestedAttributeName", nestedAttributeName);
        expressionAttributeNames.put("#uniqueAttribute", uniqueAttribute);

        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newValue", new AttributeValue(newValue));
        expressionAttributeValues.put(":uniqueValue", new AttributeValue(uniqueValue));
        expressionAttributeValues.put(":emptyList", new AttributeValue().withL(Collections.emptyList()));

        
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(key)
                .withUpdateExpression("SET " + attributeName + "[#index].#nestedAttributeName = :newValue")
                .withConditionExpression("attribute_exists(" + attributeName + "[#index])")
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues);

        try {
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (ConditionalCheckFailedException e) {
            throw new IllegalArgumentException("Update condition failed.");
        }
    }
    

}
