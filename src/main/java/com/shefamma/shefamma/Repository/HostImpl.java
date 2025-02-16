package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.shefamma.shefamma.entities.*;
import com.shefamma.shefamma.services.MealCache;
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

    public HostEntity getHost(String hostId, String geocode) {
        HostEntity host= dynamoDBMapper.load(HostEntity.class, hostId, geocode);
        return host;
    }
    public HostEntity getDataUsingPk(String pk) {
        HostEntity hashKeyValues = new HostEntity();
        hashKeyValues.setUuidHost(pk);


        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withHashKeyValues(hashKeyValues);

        List<HostEntity> result = dynamoDBMapper.query(HostEntity.class, queryExpression);

        if (!result.isEmpty()) {
            return result.get(0);  // return the first item
        } else {
            return null;  // or handle this case as you see fit
        }
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
//                attributeName = "dsec";
                break;
            case "dineCategory":
                value = hostentity.getDineCategory();
                attributeName = "dCaT";
                break;
            case "DDP":
                value = hostentity.getDDP();
                attributeName = "DDP";
                break;
            case "nameHost":
                value = hostentity.getNameHost();
                attributeName = "name";
                break;
            case "DP":
                value = hostentity.getDP();
                attributeName = "DP";
                break;
            case "descriptionHost":
                value = hostentity.getDescriptionHost();
                attributeName = "dsec";
                break;
            case "currentMessage":
                value = hostentity.getCurrentMessage();
                break;
            case "status":
                value = hostentity.getStatus();
                attributeName = "stts";
                break;
            case "providedMeals":
                value = hostentity.getProvidedMeals();
                attributeName = "provMeals";
                break;
            // Add more cases for other attributes if needed
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }

        // attributeName given to this method should be the attribute corresponding to the name in dynamodb table.
        UpdateItemResult response = commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
        System.out.println(response);

        try {
            commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
            return hostentity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Host entity. Error: " + e.getMessage());
        }
    }
    @Override
    public boolean areAddressesWithinRadius(String geoHost, String geoDelivery, double radius) {
        // Extract latitudes and longitudes from the geocodes
        String[] hostCoords = geoHost.split(",");
        String[] deliveryCoords = geoDelivery.split(",");
        double lat1 = Double.parseDouble(hostCoords[0]);
        double lon1 = Double.parseDouble(hostCoords[1]);
        double lat2 = Double.parseDouble(deliveryCoords[0]);
        double lon2 = Double.parseDouble(deliveryCoords[1]);

        // Use the existing method for calculating the distance
        return haversineDistance(lat1, lon1, lat2, lon2) <= radius;
}
    @Override
    public List<HostCardEntity> findRestaurantsWithinRadius(double latitude, double longitude, double radius) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":gpk", new AttributeValue().withS("h"));
        eav.put(":gsk", new AttributeValue().withS("host#"));
        eav.put(":statusVal", new AttributeValue().withS("true")); // Add this line

        String projectionExpression = "pk, sk";
        String filterExpression = "stts = :statusVal";

        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withIndexName("gsi1")
                .withKeyConditionExpression("gpk = :gpk AND begins_with(gsk, :gsk)")
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(eav)
                .withProjectionExpression(projectionExpression)
                .withConsistentRead(false);

        PaginatedQueryList<HostEntity> queryResult = dynamoDBMapper.query(HostEntity.class, queryExpression);

        List<HostDistancePair> hostsWithDistance = queryResult.stream()
                .map(host -> new HostDistancePair(host, calculateDistance(host.getGeocode(), latitude, longitude)))
                .filter(hostDistancePair -> hostDistancePair.distance <= radius)
                .sorted(Comparator.comparingDouble(hostDistancePair -> hostDistancePair.distance))
                .collect(Collectors.toList());

        return hostsWithDistance.stream()
                .map(hostDistancePair -> fetchFullHostDetails(hostDistancePair.host.getUuidHost(), hostDistancePair.host.getGeocode()))
                .filter(Objects::nonNull)
                .map(this::createHostCardEntity)
                .collect(Collectors.toList());
    }
    private HostCardEntity createHostCardEntity(HostEntity host) {
        String hostId = host.getUuidHost();
        String itemId = "item#" + hostId.split("#")[1];

        Set<MealEntity> cachedMeals = MealCache.getMealsByUuid(itemId);
        List<MealEntity> meals = new ArrayList<>(cachedMeals);

        // If meals were found in the cache, use them directly
        if (!cachedMeals.isEmpty()) {
            return new HostCardEntity(host, meals);
        }

        // Query the database if no cached meals are found
        DynamoDBQueryExpression<MealEntity> itemQueryExpression = new DynamoDBQueryExpression<MealEntity>()
                .withConsistentRead(false)
                .withKeyConditionExpression("pk = :pk")
                .withExpressionAttributeValues(Collections.singletonMap(":pk", new AttributeValue().withS(itemId)))
                .withScanIndexForward(true);

        List<MealEntity> queriedMeals = dynamoDBMapper.query(MealEntity.class, itemQueryExpression);

        // Cache newly queried meals
        for (MealEntity meal : queriedMeals) {
            MealCache.putMeal(meal.getUuidMeal(), meal.getNameItem(), meal);
        }

        // Return all meals, prioritizing cached meals but including any new ones from the query
        meals.addAll(queriedMeals.stream().filter(meal -> !cachedMeals.contains(meal)).collect(Collectors.toList()));
        return new HostCardEntity(host, meals);
    }

    private HostEntity fetchFullHostDetails(String pk,String sk) {

        return dynamoDBMapper.load(HostEntity.class, pk,sk);
    }
    private static class HostDistancePair {
        HostEntity host;
        double distance;

        HostDistancePair(HostEntity host, double distance) {
            this.host = host;
            this.distance = distance;
        }
    }
    private boolean isWithinRadius(String geocode, double latitude, double longitude, double radius) {
        String[] geocodeParts = geocode.split(",");
        double lat = Double.parseDouble(geocodeParts[0]);
        double lng = Double.parseDouble(geocodeParts[1]);

        double distance = haversineDistance(latitude, longitude, lat, lng);
        return distance <= radius;
    }
    private double calculateDistance(String geocode, double latitude, double longitude) {
        String[] geocodeParts = geocode.split(",");
        double lat = Double.parseDouble(geocodeParts[0]);
        double lng = Double.parseDouble(geocodeParts[1]);

        return haversineDistance(latitude, longitude, lat, lng);
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
////        Map<String, AttributeValue> eav = new HashMap<>();
////        eav.put(":pk", new AttributeValue().withS("#host"));
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
                    String hostId = host.getUuidHost();

                    String[] hostIdString = hostId.split("#");
                    String itemId = "item#" + hostIdString[1];

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

                    DynamoDBQueryExpression<MealEntity> itemQueryExpression = new DynamoDBQueryExpression<MealEntity>()
                            .withConsistentRead(false)
                            .withKeyConditionExpression("pk=:pk AND begins_with(sk, :val)")
                            .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                                put(":pk", new AttributeValue().withS(itemId));
                                put(":val", new AttributeValue().withS(itemValue));
                            }})
                            .withScanIndexForward(true);

                    List<MealEntity> meals = dynamoDBMapper.query(MealEntity.class, itemQueryExpression);
                    return new HostCardEntity(host, meals); // Create HostCardEntity with the list of MealEntity objects
                })
                .collect(Collectors.toList());
    }

//    this method has mistake, check the ends_with method, wrong logic
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

        Map<String, List<Object>> resultMap = dynamoDBMapper.batchLoad((Iterable<?>) Collections.singletonMap(HostEntity.class, hostIds));

        return resultMap.values().stream()
                .flatMap(Collection::stream)
                .map(obj -> (HostEntity) obj)
                .collect(Collectors.toList());
    }
    //sevaral changes requird
    @Override
    public OrderEntity getHostRatingReview(HostEntity hostEntity) {
        OrderEntity orderEntity = new OrderEntity();
        try {
            // Create a query expression for the GSI using the host ID as the partition key
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":hostId", new AttributeValue().withS(hostEntity.getUuidHost()));

            DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                    .withIndexName("gsi1") // Replace 'gsi_name' with the actual GSI name
                    .withConsistentRead(false) // Adjust the consistency based on your requirements
                    .withKeyConditionExpression("gpk = :hostId")
                    .withExpressionAttributeValues(eav)
                    .withProjectionExpression("rat, rev, sk, name"); // Specify the attributes to retrieve

            // Execute the query
            PaginatedQueryList<OrderEntity> result = dynamoDBMapper.query(OrderEntity.class, queryExpression);

            // Process the query result to extract ratings and reviews
            StringBuilder ratings = new StringBuilder();
            StringBuilder reviews = new StringBuilder();
            StringBuilder timeStamp = new StringBuilder();
            StringBuilder name = new StringBuilder();//name is name of guest

            for (OrderEntity order : result) {
                ratings.append(order.getRating()).append(",");
                reviews.append(order.getReview()).append(",");
                timeStamp.append(order.getTimeStamp()).append(",");
                name.append(order.getNameGuest()).append(",");
            }

            // Set the ratings and reviews in the orderEntity object
            orderEntity.setRating(!ratings.isEmpty() ? ratings.substring(0, ratings.length() - 1) : "");
            orderEntity.setReview(!reviews.isEmpty() ? reviews.substring(0, reviews.length() - 1) : "");
            orderEntity.setTimeStamp(!timeStamp.isEmpty() ? timeStamp.substring(0, timeStamp.length() - 1) : "");
            orderEntity.setNameGuest(!timeStamp.isEmpty() ? name.substring(0, name.length() - 1) : "");

        } catch (Exception e) {
            System.out.println("Error occurred while fetching host rating and review: " + e.getMessage());
            e.printStackTrace();
        }
        return orderEntity;
    }

    @Override
    public HostEntity updateHostRating(String hostId,String geoHost, double userRating) {
        // Create a query expression to fetch the existing rating and number of ratings
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":hostId", new AttributeValue().withS(hostId));

        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withKeyConditionExpression("pk = :hostId")
                .withExpressionAttributeValues(eav)
                .withProjectionExpression("ratH,noOfRat"); // Specify the attributes to retrieve

        // Execute the query to fetch the existing rating and number of ratings
        PaginatedQueryList<HostEntity> result = dynamoDBMapper.query(HostEntity.class, queryExpression);

        // Retrieve the HostEntity object from the query result
        HostEntity hostEntity = result.get(0); // Assuming only one result is expected

        // Calculate the new average rating and update the number of ratings
        int numberOfRatings;
        double existingRating;
        double newRatingSum;
        double newAverageRating;

        if (hostEntity.getNoOfRating() == null) {
            numberOfRatings = 1;

            newAverageRating = userRating;
        } else {
            numberOfRatings = Integer.parseInt(hostEntity.getNoOfRating());

            existingRating = Double.parseDouble(hostEntity.getRatingHost());
            newRatingSum = existingRating * numberOfRatings + userRating;
            numberOfRatings++;
            newAverageRating = newRatingSum / (numberOfRatings);
        }

        // Update the hostEntity object with the new rating and number of ratings
        hostEntity.setRatingHost(String.valueOf(newAverageRating));
        hostEntity.setNoOfRating(String.valueOf(numberOfRatings));


        Map<String, String> attributeUpdates = new HashMap<>();
        attributeUpdates.put("ratH", String.valueOf(newAverageRating));
        attributeUpdates.put("noOfRat", String.valueOf(numberOfRatings));

        commonMethods.updateMultipleAttributes(hostId,geoHost, attributeUpdates);
//        commonMethods.updateMultipleAttributes(hostId,"13.060091,80.286565", attributeUpdates);
        return hostEntity;
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

