package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.ItemEntity;
import com.shefamma.shefamma.entities.TimeSlotEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

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
    public HostEntity getHosts(String hostId, String sort) {
        return null;
    }

    @Override
    public HostEntity update(String partition, String sort, HostEntity hostentity) {
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
                .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)))
                .withExpectedEntry("sk", new ExpectedAttributeValue(new AttributeValue(sort)));

        dynamoDBMapper.save(hostentity, saveExpression);
        System.out.println("yes");
        return hostentity;
    }

    @Override
    public List<HostEntity> getHostsItemSearchFilter(String itemValue) {
        DynamoDBQueryExpression<ItemEntity> queryExpression = new DynamoDBQueryExpression<ItemEntity>()
                .withConsistentRead(false)
                .withKeyConditionExpression("ends_with(pk, :pk) AND dishCategory = :val")
                .withProjectionExpression("uuidItem")
                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                    put(":pk", new AttributeValue().withS("#item"));
                    put(":val", new AttributeValue().withS(itemValue));
                }})
                .withScanIndexForward(true);

        List<ItemEntity> items = dynamoDBMapper.query(ItemEntity.class, queryExpression);

        List<HostEntity> hosts = new ArrayList<>();
        for (ItemEntity item : items) {
            String id;
            String[] splitted;
           id=item.getUuidItem();
           splitted=id.split("#");
            hosts.add(dynamoDBMapper.load(HostEntity.class, splitted[0]+"#host"));
        }
        return hosts;
    }

    @Override
    public List<HostEntity> getHostsCategorySearchFilter(String dineCategoryValue) {
        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withConsistentRead(false)
                .withKeyConditionExpression("ends_with(pk, :pk) AND dineCategory = :val")

                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                    put(":pk", new AttributeValue().withS("#host"));
                    put(":val", new AttributeValue().withS(dineCategoryValue));
                }})
                .withScanIndexForward(true);
        return dynamoDBMapper.query(HostEntity.class, queryExpression);
    }

    @Override
    public List<HostEntity> getHostsTimeSlotSearchFilter(int t1, int t2,String timeDuration ) {
        DynamoDBQueryExpression<TimeSlotEntity> queryExpression = new DynamoDBQueryExpression<TimeSlotEntity>()
                .withConsistentRead(false)
                .withKeyConditionExpression("ends_with(pk, :pk) and #duration = :duration")
                .withFilterExpression("slots[0].startTime BETWEEN :t1 AND :t2")
                .withProjectionExpression("pk")
                .withExpressionAttributeNames(new HashMap<String, String>() {{
                    put("#duration", "sk");
                }})
                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                    put(":pk", new AttributeValue().withS("#time"));
                    put(":duration", new AttributeValue().withS(timeDuration));
                    put(":t1", new AttributeValue().withN(String.valueOf(t1)));
                    put(":t2", new AttributeValue().withN(String.valueOf(t2)));
                }})
                .withScanIndexForward(true);

        List<TimeSlotEntity> items = dynamoDBMapper.query(TimeSlotEntity.class, queryExpression);

        List<HostEntity> hosts = new ArrayList<>();
        for (TimeSlotEntity item : items) {
            String id;
            String[] splitted;
            id=item.getUuidTime();
            splitted=id.split("#");
            hosts.add(dynamoDBMapper.load(HostEntity.class, splitted[0]+"#host"));
        }
        return hosts;
    }

}
//    @Override
//    public List<HostEntity> getHostsTimeSlotSearchFilter(int t1, int t2, String timeDuration) {
//        DynamoDBQueryExpression<TimeSlotEntity> queryExpression = new DynamoDBQueryExpression<TimeSlotEntity>()
//                .withConsistentRead(false)
//                .withKeyConditionExpression("ends_with(pk, :pk) and #duration = :duration")
//                .withFilterExpression("slots[0].startTime BETWEEN :t1 AND :t2")
//                .withProjectionExpression("pk")
//                .withExpressionAttributeNames(new HashMap<String, String>() {{
//                    put("#duration", "sk");
//                }})
//                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
//                    put(":pk", new AttributeValue().withS("#time"));
//                    put(":duration", new AttributeValue().withS(timeDuration));
//                    put(":t1", new AttributeValue().withN(String.valueOf(t1)));
//                    put(":t2", new AttributeValue().withN(String.valueOf(t2)));
//                }})
//                .withScanIndexForward(true);
//
//        List<TimeSlotEntity> timeSlots = dynamoDBMapper.query(TimeSlotEntity.class, queryExpression);
//
//        Map<String, List<Object>> itemsToLoad = new HashMap<>();
//        for (TimeSlotEntity timeSlot : timeSlots) {
//            String uuidTime = timeSlot.getUuidTime();
//            String[] splitted = uuidTime.split("#");
//            String hostPk = splitted[0] + "#host";
//            if (!itemsToLoad.containsKey(hostPk)) {
//                itemsToLoad.put(hostPk, new ArrayList<>());
//            }
//            itemsToLoad.get(hostPk).add(uuidTime);
//        }
//
//        List<HostEntity> hosts = new ArrayList<>();
//        for (Map.Entry<String, List<Object>> entry : itemsToLoad.entrySet()) {
//            String hostPk = entry.getKey();
//            List<Object> uuidTimes = entry.getValue();
//            HostEntity host = dynamoDBMapper.load(HostEntity.class, hostPk);
//            if (host != null) {
//                List<TimeSlotEntity> timeSlotEntities = dynamoDBMapper.batchLoad(
//                        uuidTimes.stream()
//                                .map(uuid -> new TimeSlotEntity().withUuidTime((String) uuid))
//                                .collect(Collectors.toList())
//                ).get(TimeSlotEntity.class.getSimpleName());
//                if (timeSlotEntities != null) {
//                    host.setTimeSlots(timeSlotEntities);
//                }
//                hosts.add(host);
//            }
//        }
//
//
//
//
//        return hosts;
//    }
//}
// code for batchLoad()**************************************************************
//DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
//
//    // Create a map of primary keys to load
//    Map<Class<?>, List<KeyPair>> keyPairMap = new HashMap<>();
//    List<KeyPair> keyPairs = new ArrayList<>();
//keyPairs.add(new KeyPair().withHashKey("hashkey1").withRangeKey("rangekey1"));
//        keyPairs.add(new KeyPair().withHashKey("hashkey2").withRangeKey("rangekey2"));
//        keyPairMap.put(MyItemClass.class, keyPairs);
//
//// Load the items
//        Map<Class<?>, List<Object>> items = dynamoDBMapper.batchLoad(keyPairMap, new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
//        List<MyItemClass> myItems = (List<MyItemClass>) items.get(MyItemClass.class);
//************************************************************************************************

