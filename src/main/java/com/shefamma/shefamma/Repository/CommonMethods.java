package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CommonMethods {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

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
}
