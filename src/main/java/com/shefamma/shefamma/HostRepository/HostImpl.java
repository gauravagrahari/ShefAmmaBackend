package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.HostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HostImpl implements Host {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;


    public HostEntity saveHost(HostEntity host) {
        dynamoDBMapper.save(host);
        return host;
    }

    public HostEntity getHost(String hostId, String sort) {
        return dynamoDBMapper.load(HostEntity.class, hostId, sort);
    }

    @Override
    public HostEntity update(String partition, String sort, HostEntity hostentity) {
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
                .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)))
                .withExpectedEntry("sk", new ExpectedAttributeValue(new AttributeValue(sort)));

        dynamoDBMapper.save(hostentity, saveExpression);
        return hostentity;
    }
}

//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//        import com.amazonaws.services.dynamodbv2.model.AttributeValue;
//        import java.util.HashMap;
//        import java.util.Map;
//
//public class CommonImpl {
//
//    private DynamoDBMapper dynamoDBMapper;
//
//    public void updateAttribute(String attributeName, String partitionKey, String sortKey, Object value) {
//        Map<String, AttributeValue> key = new HashMap<>();
//        key.put("partition_key", new AttributeValue(partitionKey));
//        key.put("sort_key", new AttributeValue(sortKey));
//
//        Map<String, AttributeValue> attributeValues = new HashMap<>();
//        attributeValues.put(":value", new AttributeValue(value.toString()));
//
//        Map<String, String> attributeNames = new HashMap<>();
//        attributeNames.put("#attr", attributeName);
//
//        MyTableItem item = new MyTableItem();
//        item.setPartitionKey(partitionKey);
//        item.setSortKey(sortKey);
//        item.setAttribute(attributeValue);
//
//        dynamoDBMapper.save(item, new DynamoDBSaveExpression()
//                .withConditionExpression("attribute_exists(partition_key) AND attribute_exists(sort_key)")
//                .withExpressionAttributeNames(attributeNames)
//                .withExpressionAttributeValues(attributeValues));
//    }
//}

