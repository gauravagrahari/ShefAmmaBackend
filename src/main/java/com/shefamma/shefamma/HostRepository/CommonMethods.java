package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.utils.ImmutableMap;

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


    /**
     * Updates a specific attribute in a DynamoDB table using the primary key.

     * @param partitionKeyValue The value of the partition key attribute.
     * @param attributeName     The name of the attribute to update.
     * @param newValue          The new value for the attribute.
     */
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
            // Handle exceptions as appropriate for your application
            e.printStackTrace();
        }
    }
    /**
     * Updates a specific attribute in a DynamoDB table using the primary key (partition key and sort key).
     * @param partitionKeyValue The value of the partition key attribute.
     * @param sortKeyValue      The value of the sort key attribute.
     * @param attributeName     The name of the attribute to update.
     * @param newValue          The new value for the attribute.
     */
    public void updateAttributeWithSortKey(String partitionKeyValue, String sortKeyValue, String attributeName, String newValue) {
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
                    .withExpressionAttributeValues(expressionAttributeValues);

            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (Exception e) {
            // Handle exceptions as appropriate for your application
            e.printStackTrace();
        }
    }

    public void updateSpecificAttributeOrderEntity(String primaryKey, String primaryValue, String attributeName, String nestedAttributeName, String uniqueAttribute, String uniqueValue, String newValue) {
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
                .withUpdateExpression("SET " + attributeName + "[#index].#nestedAttributeName = " + attributeName + "[#index].#nestedAttributeName - :newValue")
                .withConditionExpression("attribute_exists(" + attributeName + "[#index]) AND " + attributeName + "[#index].#nestedAttributeName >= :newValue")
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues);

        try {
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (ConditionalCheckFailedException e) {
            throw new IllegalArgumentException("New capacity is greater than current capacity - " + newValue);
        }
    }


    /**
     * Updates multiple attributes of an item in the DynamoDB table.
     *
     * @param partitionKeyValue  The value of the partition key.
     * @param attributeValues    A map of attribute names and their new values.
     */
    public void updateMulyipleAttributes(String partitionKeyValue, Map<String, String> attributeValues) {
        try {
            // Create the key map for the partition key
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(partitionKeyValue));

            // Create the map for expression attribute values
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            // Create the update expression string
            String updateExpression = "SET ";

            int index = 0;
            // Iterate over the attribute values map
            for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
                String attributeName = entry.getKey();
                String newValue = entry.getValue();
                String placeholder = ":newValue" + index;

                // Add the attribute value to the expression attribute values map
                expressionAttributeValues.put(placeholder, new AttributeValue(newValue));

                // Add the attribute name and placeholder to the update expression
                updateExpression += attributeName + " = " + placeholder + ", ";

                index++;
            }

            // Remove the trailing comma and space from the update expression
            updateExpression = updateExpression.substring(0, updateExpression.length() - 2);

            // Create the UpdateItemRequest
            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(tableName)
                    .withKey(key)
                    .withUpdateExpression(updateExpression)
                    .withExpressionAttributeValues(expressionAttributeValues);

            // Update the item in the DynamoDB table
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (Exception e) {
            // Handle exceptions as appropriate for your application
            e.printStackTrace();
        }
    }
    /**
     * Updates a specific attribute of a nested JSON object based on the unique attribute of the nested JSON object.
     *
     * @param primaryKey         The primary key attribute name.
     * @param primaryValue       The primary key attribute value.
     * @param attributeName      The attribute name containing the nested JSON object.
     * @param nestedAttributeName The attribute name within the nested JSON object that needs to be updated.
     * @param uniqueAttribute    The unique attribute name within the nested JSON object.
     * @param uniqueValue        The unique attribute value used to identify the specific nested JSON object.
     * @param newValue           The new value to set for the specified attribute within the nested JSON object.
     * @throws IllegalArgumentException if the update condition fails.
     */
    public void updateSpecificNestedAttribute(String primaryKey, String primaryValue, String attributeName, String nestedAttributeName, String uniqueAttribute, String uniqueValue, String newValue) {
        // Create the key for the update request
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(primaryKey, new AttributeValue(primaryValue));

        // Define the expression attribute names for placeholders
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#index", "list_append(" + attributeName + ", :emptyList)[0]");
        expressionAttributeNames.put("#nestedAttributeName", nestedAttributeName);
        expressionAttributeNames.put("#uniqueAttribute", uniqueAttribute);

        // Define the expression attribute values for placeholders
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newValue", new AttributeValue(newValue));
        expressionAttributeValues.put(":uniqueValue", new AttributeValue(uniqueValue));
        expressionAttributeValues.put(":emptyList", new AttributeValue().withL(Collections.emptyList()));

        // Create the update request
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
