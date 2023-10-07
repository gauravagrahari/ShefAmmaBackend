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
//*******************
//the methods below can be modified to return boolean on success or failure
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
     *
     * @param partitionKeyValue The value of the partition key attribute.
     * @param sortKeyValue      The value of the sort key attribute.
     * @param attributeName     The name of the attribute to update, it is the value present in the dynamoDb table not the variable name in the Entity class.
     * @param newValue          The new value for the attribute.
     */
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
                    .withReturnValues(ReturnValue.ALL_NEW); // Add this line

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

    //    public void updateSpecificAttributeOrderEntity(String primaryKey, String primaryValue, String attributeName, String nestedAttributeName, String uniqueAttribute, String uniqueValue,String newValue,String index) {
//        Map<String, AttributeValue> key = new HashMap<>();
//        key.put(primaryKey, new AttributeValue(primaryValue));
//
//        Map<String, String> expressionAttributeNames = new HashMap<>();
//        expressionAttributeNames.put("#index", "list_append(" + attributeName + ", :emptyList)[0]");
//        expressionAttributeNames.put("#nestedAttributeName", nestedAttributeName);
//        expressionAttributeNames.put("#uniqueAttribute", uniqueAttribute);
//
//        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
//        expressionAttributeValues.put(":newValue", new AttributeValue(newValue));
//        expressionAttributeValues.put(":uniqueValue", new AttributeValue(uniqueValue));
//        expressionAttributeValues.put(":emptyList", new AttributeValue().withL(Collections.emptyList()));
//
//        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
//                .withTableName(tableName)
//                .withKey(key)
//                .withUpdateExpression("SET " + attributeName + "[#index].#nestedAttributeName = " + attributeName + "[#index].#nestedAttributeName - :newValue")
//                .withConditionExpression("attribute_exists(" + attributeName + "[#index]) AND " + attributeName + "[#index].#nestedAttributeName >= :newValue")
//                .withExpressionAttributeNames(expressionAttributeNames)
//                .withExpressionAttributeValues(expressionAttributeValues);
//
//        try {
//            amazonDynamoDB.updateItem(updateItemRequest);
//        } catch (ConditionalCheckFailedException e) {
//            throw new IllegalArgumentException("New capacity is greater than current capacity - " + newValue);
//        }
//    }
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
//this method has got mistakes
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

//        try {
//            amazonDynamoDB.updateItem(updateItemRequest);
//        } catch (ConditionalCheckFailedException e) {
//            throw new IllegalArgumentException("New capacity is greater than current capacity - " + newValue);
//        }
    }
    /**
     * Updates multiple attributes of an item in the DynamoDB table.
     *
     * @param partitionKeyValue  The value of the partition key.
     * @param attributeValues    A map of attribute names and their new values.
     */
    public void updateMultipleAttributes(String partitionKeyValue, Map<String, String> attributeValues) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(partitionKeyValue));

            // Create the map for expression attribute values
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            // Create the update expression string
            StringBuilder updateExpression = new StringBuilder("SET ");

            int index = 0;
            // Iterate over the attribute values map
            for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
                String attributeName = entry.getKey();
                String newValue = entry.getValue();
                String placeholder = ":newValue" + index;

                // Add the attribute value to the expression attribute values map
                expressionAttributeValues.put(placeholder, new AttributeValue(newValue));

                // Add the attribute name and placeholder to the update expression
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
            // Handle exceptions as appropriate
            e.printStackTrace();
        }
    }

//    com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException: Invalid UpdateExpression: Attribute name is a reserved keyword; reserved keyword: status (Service: AmazonDynamoDBv2; Status Code: 400; Error Code: ValidationException; Request ID: NQFDSRKKKGJ9EK9DP33IMBQMPBVV4KQNSO5AEMVJF66Q9ASUAAJG; Proxy: null)
//    at com.amazonaws.http.AmazonHttpClient$RequestExecutor.handleErrorResponse(AmazonHttpClient.java:1862)
//    below method gave this error

    public void updateMultipleAttributes(String partitionKeyValue, String sortKeyValue, Map<String, String> attributeValues) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("pk", new AttributeValue(partitionKeyValue));
            key.put("sk", new AttributeValue(sortKeyValue));

            // Create the map for expression attribute values
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            // Create the update expression string
            StringBuilder updateExpression = new StringBuilder("SET ");

            int index = 0;
            // Iterate over the attribute values map
            for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
                String attributeName = entry.getKey();
                String newValue = entry.getValue();
                String placeholder = ":newValue" + index;

                // Add the attribute value to the expression attribute values map
                expressionAttributeValues.put(placeholder, new AttributeValue(newValue));

                // Add the attribute name and placeholder to the update expression
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

            // Update the item in the DynamoDB table
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (Exception e) {
            // Handle exceptions as appropriate
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
