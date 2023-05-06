package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class ItemImpl implements Item{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Override
    public ItemEntity saveItem(ItemEntity itementity) {
        itementity.setHostId_Item(itementity.getHostId_Item()+"#item");
        dynamoDBMapper.save(itementity);
        return itementity;
    }

    @Override
    public ItemEntity updateItem(String partition, String sort, ItemEntity itementity) {
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
            .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)))
            .withExpectedEntry("sk", new ExpectedAttributeValue(new AttributeValue(sort)));

    dynamoDBMapper.save(itementity, saveExpression);
    return itementity;
    }

    @Override
    public List<ItemEntity> getItems(String hostId, ItemEntity itementity) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pk", new AttributeValue().withS(hostId));
        expressionAttributeValues.put(":sk", new AttributeValue().withS("#item"));

        DynamoDBQueryExpression<ItemEntity> queryExpression = new DynamoDBQueryExpression<ItemEntity>()
                .withKeyConditionExpression("pk = :pk and begins_with(sk, :sk)") //we need to use the attribute available in the dynamodbtable
                .withExpressionAttributeValues(expressionAttributeValues);

        return dynamoDBMapper.query(ItemEntity.class, queryExpression);
    }

    @Override
    public ItemEntity getItem(String hostId, String nameItem, ItemEntity itementity) {
        return dynamoDBMapper.load(ItemEntity.class,hostId,nameItem);
    }
}

