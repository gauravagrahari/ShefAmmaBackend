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

@Repository
public class CommonMethods {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public void updateSpecificAttribute(String tableName, String primaryKey, String primaryValue, String attributeName, String newValue) {
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(Collections.singletonMap(primaryKey, new AttributeValue(primaryValue)))
                .withUpdateExpression("SET " + attributeName + " = :newValue")
                .withExpressionAttributeValues(Collections.singletonMap(":newValue", new AttributeValue(newValue)));
        amazonDynamoDB.updateItem(updateItemRequest);
    }
    public void updateSpecificAttribute(String tableName, String primaryKey, String primaryValue, String attributeName, String nestedAttributeName, String uniqueAttribute, String uniqueValue, String newValue) {
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(Collections.singletonMap(primaryKey, new AttributeValue(primaryValue)))
                .withUpdateExpression("SET " + attributeName + "[#index].#nestedAttributeName = " + attributeName + "[#index].#nestedAttributeName - :newValue")
                .withConditionExpression("attribute_exists(" + attributeName + "[#index]) AND " + attributeName + "[#index].#nestedAttributeName >= :newValue")
                .withExpressionAttributeNames(ImmutableMap.of(
                        "#index", "list_append(" + attributeName + ", :emptyList)[0]",
                        "#nestedAttributeName", nestedAttributeName,
                        "#uniqueAttribute", uniqueAttribute
                ))
                .withExpressionAttributeValues(ImmutableMap.of(
                        ":newValue", new AttributeValue(newValue),
                        ":uniqueValue", new AttributeValue(uniqueValue),
                        ":emptyList", new AttributeValue().withL(Collections.emptyList())
                ));

        try {
            amazonDynamoDB.updateItem(updateItemRequest);
        } catch (ConditionalCheckFailedException e) {
            throw new IllegalArgumentException("New capacity is greater than current capacity - " + newValue);
        }
    }


}
