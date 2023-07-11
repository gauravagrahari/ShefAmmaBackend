package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class HostImpl implements Host {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private CommonMethods commonMethods;
    @Autowired
    private HostCardEntity hostCardEntity;
    @Autowired
    private OrderEntity orderEntity;


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
    public HostEntity update(String partition, String sort, String attributeName, HostEntity hostentity) {
        String value = null;
        // Get the value of the specified attribute
        switch (attributeName) {
            case "geocode":
                value = hostentity.getGeocode();
                break;
            case "dineCategory":
                value = hostentity.getDineCategory();
                break;
//            case "DDP":
//                value = hostentity.getDDP();
//                break;
            case "nameHost":
                value = hostentity.getNameHost();
                break;
//            case "DP":
//                value = hostentity.getDP();
//                break;
            case "descriptionHost":
                value = hostentity.getDescriptionHost();
                break;
            case "currentMessage":
                value = hostentity.getCurrentMessage();
                break;
            // Add more cases for other attributes if needed
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        commonMethods.updateAttribute(partition,attributeName,value);
        return hostentity;
    }
    @Override
    public List<HostCardEntity> findRestaurantsWithinRadius(double latitude, double longitude, double radius) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":gpk", new AttributeValue().withS("h"));
        eav.put(":gsk", new AttributeValue().withS("host#"));

        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withIndexName("gsi1")
                .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        PaginatedQueryList<HostEntity> queryResult = dynamoDBMapper.query(HostEntity.class, queryExpression);


        return queryResult.stream()
                .filter(restaurant -> {
                    String[] geocode = restaurant.getGeocode().split(",");
                    double lat = Double.parseDouble(geocode[0]);
                    double lng = Double.parseDouble(geocode[1]);

                    double distance = haversineDistance(latitude, longitude, lat, lng);
                    return distance <= radius;
                })
                .map(host -> {
                    String hostId = host.getUuidHost();
                    String[] hostIdString = hostId.split("#");
                    String itemId = "item#" + hostIdString[1];

                    DynamoDBQueryExpression<ItemEntity> itemQueryExpression = new DynamoDBQueryExpression<ItemEntity>()
                            .withConsistentRead(false)
                            .withKeyConditionExpression("pk=:pk")
                            .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                                put(":pk", new AttributeValue().withS(itemId));
                            }})
                            .withScanIndexForward(true);

                    List<ItemEntity> items = dynamoDBMapper.query(ItemEntity.class, itemQueryExpression);
                    List<String> itemNames = items.stream().map(ItemEntity::getNameItem).collect(Collectors.toList());

                    return new HostCardEntity(host, itemNames);
                })
                .collect(Collectors.toList());
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    @Override
    public List<HostCardEntity> getHostsItemSearchFilter(double latitude, double longitude, double radius, String itemValue) {
//        Map<String, AttributeValue> eav = new HashMap<>();
//        eav.put(":pk", new AttributeValue().withS("#host"));
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":gpk", new AttributeValue().withS("h"));
        eav.put(":gsk", new AttributeValue().withS("host#"));

        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withIndexName("gsi1")
                .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        PaginatedQueryList<HostEntity> queryResult = dynamoDBMapper.query(HostEntity.class, queryExpression);

        return queryResult.stream()
                .filter(host -> {
                    String[] geocode = host.getGeocode().split(",");
                    double hostLat = Double.parseDouble(geocode[0]);
                    double hostLng = Double.parseDouble(geocode[1]);

                    double distance = haversineDistance(latitude, longitude, hostLat, hostLng);
                    return distance <= radius;
                })
                .filter(host -> {
                    String hostId=host.getUuidHost();

                    String[] hostIdString =hostId.split("#");
                   String  itemId="item#"+hostIdString[1];

                    DynamoDBQueryExpression<ItemEntity> itemQueryExpression = new DynamoDBQueryExpression<ItemEntity>()
                            .withConsistentRead(false)
                            .withKeyConditionExpression("pk=:pk AND sk = :val")
                            .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                                put(":pk", new AttributeValue().withS(itemId));
                                put(":val", new AttributeValue().withS(itemValue));
                            }})
                            .withScanIndexForward(true);

                    List<ItemEntity> items = dynamoDBMapper.query(ItemEntity.class, itemQueryExpression);
                    return !items.isEmpty();
                })
                .map(host -> {
                    String hostId = host.getUuidHost();
                    String[] hostIdString = hostId.split("#");
                    String itemId = "item#" + hostIdString[1];

                    DynamoDBQueryExpression<ItemEntity> itemQueryExpression = new DynamoDBQueryExpression<ItemEntity>()
                            .withConsistentRead(false)
                            .withKeyConditionExpression("pk=:pk AND begins_with(sk, :val)")
                            .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                                put(":pk", new AttributeValue().withS(itemId));
                                put(":val", new AttributeValue().withS(itemValue));
                            }})
                            .withScanIndexForward(true);

                    List<ItemEntity> items = dynamoDBMapper.query(ItemEntity.class, itemQueryExpression);
                    List<String> itemNames = items.stream().map(ItemEntity::getNameItem).collect(Collectors.toList());


                    return new HostCardEntity(host, itemNames);
                })
                .collect(Collectors.toList());
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
    public List<HostEntity> getHostsCategorySearchFilter(double latitude, double longitude, double radius, String dineCategoryValue) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":pk", new AttributeValue().withS("#host"));
        eav.put(":val", new AttributeValue().withS(dineCategoryValue));

        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withKeyConditionExpression("ends_with(pk, :pk) AND dineCategory = :val")
                .withExpressionAttributeValues(eav);

        PaginatedQueryList<HostEntity> queryResult = dynamoDBMapper.query(HostEntity.class, queryExpression);

        return queryResult.stream()
                .filter(host -> {
                    String[] geocode = host.getGeocode().split(",");
                    double hostLat = Double.parseDouble(geocode[0]);
                    double hostLng = Double.parseDouble(geocode[1]);

                    double distance = haversineDistance(latitude, longitude, hostLat, hostLng);
                    return distance <= radius;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<HostEntity> getHostsTimeSlotSearchFilter(int t1, int t2, String timeDuration) {
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

        List<String> hostIds = new ArrayList<>();
        for (TimeSlotEntity item : items) {
            String id;
            String[] splitted;
            id = item.getUuidTime();
            splitted = id.split("#");
            hostIds.add(splitted[0] + "#host");
        }

        Map<String, List<Object>> resultMap = dynamoDBMapper.batchLoad((Iterable<? extends Object>) Collections.singletonMap(HostEntity.class, hostIds));

        return resultMap.values().stream()
                .flatMap(Collection::stream)
                .map(obj -> (HostEntity) obj)
                .collect(Collectors.toList());
    }

    //sevaral changes requird
    @Override
    public OrderEntity getHostRatingReview(HostEntity hostEntity) {
        // Create a query expression for the GSI using the host ID as the partition key
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":hostId", new AttributeValue().withS(hostEntity.getUuidHost()));

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName("gsi1") // Replace 'gsi_name' with the actual GSI name
                .withConsistentRead(false) // Adjust the consistency based on your requirements
                .withKeyConditionExpression("gpk = :hostId")
                .withExpressionAttributeValues(eav)
                .withProjectionExpression("rat, rev"); // Specify the attributes to retrieve

        // Execute the query
        PaginatedQueryList<OrderEntity> result = dynamoDBMapper.query(OrderEntity.class, queryExpression);

        // Process the query result to extract ratings and reviews
        List<String> ratings = new ArrayList<>();
        List<String> reviews = new ArrayList<>();

        for (OrderEntity order : result) {
            ratings.add(order.getRating());
            reviews.add(order.getReview());
        }

        // Set the ratings and reviews in the orderEntity object
        orderEntity.setRating(ratings.toString());
        orderEntity.setReview(reviews.toString());

        return orderEntity;
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

