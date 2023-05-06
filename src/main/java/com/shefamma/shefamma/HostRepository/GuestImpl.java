package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GuestImpl implements Guest {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override 
    public GuestEntity saveGuest(GuestEntity guestentity) {
        dynamoDBMapper.save(guestentity);
        return guestentity;
    }

    @Override
    public GuestEntity getGuest(String guestId, String nameGuest) {
        return dynamoDBMapper.load(GuestEntity.class, guestId, nameGuest);
    }

//    Use the below code if update condition also involves sort key
    public GuestEntity updateGuest(String partition, String sort, GuestEntity guestEntity) {
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
                .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)))
                .withExpectedEntry("sk", new ExpectedAttributeValue(new AttributeValue(sort)));

        dynamoDBMapper.save(guestEntity, saveExpression);
        return guestEntity;
    }

//    **************************
//    **************************


//    public GuestEntity updateGuest(String guestId, String nameGuest, GuestEntity guestentity,String value,String attributeName) {
//        Table table = dynamoDB.getTable(tableName);
//        PrimaryKey primaryKey = new PrimaryKey("partition_key", guestId, "sort_key", nameGuest);
//
//        Map<String, String> attributeNames = new HashMap<>();
//        attributeNames.put("#attr", attributeName);
//
//        Map<String, Object> attributeValues = new HashMap<>();
//        attributeValues.put(":value", value);
//
//        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
//                .withPrimaryKey(primaryKey)
//                .withUpdateExpression("SET #attr = :value")
//                .withNameMap(attributeNames)
//                .withValueMap(attributeValues)
//                .withConditionExpression("attribute_exists(partition_key) AND attribute_exists(sort_key)")
//                .withReturnValues(ReturnValue.ALL_NEW);
//
//        Item updatedItem = (Item) table.updateItem(updateItemSpec).getItem();
//        List<Item> guestEntity = ItemUtils.toItemList(updatedItem);
////        GuestEntity updatedHostEntity = updatedItem.as(GuestEntity.class);
//        return guestEntity;}
}
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//        import com.amazonaws.services.dynamodbv2.model.AttributeValue;
//        import java.util.HashMap;
//        import java.util.Map;
//
//public class CommonImpl {
//

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