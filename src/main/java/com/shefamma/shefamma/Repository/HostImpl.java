package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
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

    public HostEntity saveHost(HostEntity host) {
        dynamoDBMapper.save(host);
        return host;
    }

    public HostEntity getHost(String hostId, String geocode) {
        return dynamoDBMapper.load(HostEntity.class, hostId, geocode);
    }
    public HostEntity getDataUsingPk(String pk) {
        HostEntity hashKeyValues = new HostEntity();
        hashKeyValues.setUuidHost(pk);


        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withHashKeyValues(hashKeyValues);

        List<HostEntity> result = dynamoDBMapper.query(HostEntity.class, queryExpression);

        if (!result.isEmpty()) {
            return result.get(0);  
        } else {
            return null;  
        }
    }

    @Override
    public HostEntity getHosts(String hostId, String sort) {
        return null;
    }


    @Override
    public HostEntity update(String partition, String sort, String attributeName, HostEntity hostentity) {
        String value;

        switch (attributeName) {
            case "geocode" -> value = hostentity.getGeocode();
            case "dineCategory" -> {
                value = hostentity.getDineCategory();
                attributeName = "dCaT";
            }
            case "DDP" -> {
                value = hostentity.getDDP();
                attributeName = "DDP";
            }
            case "nameHost" -> {
                value = hostentity.getNameHost();
                attributeName = "name";
            }
            case "DP" -> {
                value = hostentity.getDP();
                attributeName = "DP";
            }
            case "descriptionHost" -> {
                value = hostentity.getDescriptionHost();
                attributeName = "dsec";
            }
            case "currentMessage" -> value = hostentity.getCurrentMessage();
            case "status" -> {
                value = hostentity.getStatus();
                attributeName = "stts";
            }
            case "providedMeals" -> {
                value = hostentity.getProvidedMeals();
                attributeName = "provMeals";
            }
            default -> throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);

        try {
            commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
            return hostentity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Host entity. Error: " + e.getMessage());
        }
    }
    @Override
    public boolean areAddressesWithinRadius(String geoHost, String geoDelivery, double radius) {
        
        String[] hostCoords = geoHost.split(",");
        String[] deliveryCoords = geoDelivery.split(",");
        double lat1 = Double.parseDouble(hostCoords[0]);
        double lon1 = Double.parseDouble(hostCoords[1]);
        double lat2 = Double.parseDouble(deliveryCoords[0]);
        double lon2 = Double.parseDouble(deliveryCoords[1]);

        
        return haversineDistance(lat1, lon1, lat2, lon2) <= radius;
}
    @Override
    public List<HostCardEntity> findRestaurantsWithinRadius(double latitude, double longitude, double radius) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":gpk", new AttributeValue().withS("h"));
        eav.put(":gsk", new AttributeValue().withS("host#"));
        eav.put(":statusVal", new AttributeValue().withS("true")); 

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

        return queryResult.stream()
                .filter(restaurant -> isWithinRadius(restaurant.getGeocode(), latitude, longitude, radius))
                .map(host -> fetchFullHostDetails(host.getUuidHost(),host.getGeocode()))
                .filter(Objects::nonNull) 
                .map(this::createHostCardEntity)
                .collect(Collectors.toList());
    }

    private HostEntity fetchFullHostDetails(String pk,String sk) {

        return dynamoDBMapper.load(HostEntity.class, pk,sk);
    }

    private boolean isWithinRadius(String geocode, double latitude, double longitude, double radius) {
        String[] geocodeParts = geocode.split(",");
        double lat = Double.parseDouble(geocodeParts[0]);
        double lng = Double.parseDouble(geocodeParts[1]);

        double distance = haversineDistance(latitude, longitude, lat, lng);
        return distance <= radius;
    }

    private HostCardEntity createHostCardEntity(HostEntity host) {
        String hostId = host.getUuidHost();
        String itemId = "item#" + hostId.split("#")[1];

        DynamoDBQueryExpression<MealEntity> itemQueryExpression = new DynamoDBQueryExpression<MealEntity>()
                .withConsistentRead(false)
                .withKeyConditionExpression("pk = :pk")
                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                    put(":pk", new AttributeValue().withS(itemId));
                }})
                .withScanIndexForward(true);

        List<MealEntity> meals = dynamoDBMapper.query(MealEntity.class, itemQueryExpression);

        return new HostCardEntity(host, meals);






    }
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 
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
                    return new HostCardEntity(host, meals); 
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<HostEntity> getHostsCategorySearchFilter(String dineCategoryValue) {
        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withConsistentRead(false)
                .withKeyConditionExpression("ends_with(pk, :pk) AND dineCategory = :val")

                .withExpressionAttributeValues(new HashMap<>() {{
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
    public OrderEntity getHostRatingReview(HostEntity hostEntity) {
        OrderEntity orderEntity = new OrderEntity();
        try {
            
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":hostId", new AttributeValue().withS(hostEntity.getUuidHost()));

            DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                    .withIndexName("gsi1") 
                    .withConsistentRead(false) 
                    .withKeyConditionExpression("gpk = :hostId")
                    .withExpressionAttributeValues(eav)
                    .withProjectionExpression("rat, rev, sk, name"); 

            
            PaginatedQueryList<OrderEntity> result = dynamoDBMapper.query(OrderEntity.class, queryExpression);

            
            StringBuilder ratings = new StringBuilder();
            StringBuilder reviews = new StringBuilder();
            StringBuilder timeStamp = new StringBuilder();
            StringBuilder name = new StringBuilder();

            for (OrderEntity order : result) {
                ratings.append(order.getRating()).append(",");
                reviews.append(order.getReview()).append(",");
                timeStamp.append(order.getTimeStamp()).append(",");
                name.append(order.getNameGuest()).append(",");
            }

            
            orderEntity.setRating(!ratings.isEmpty() ? ratings.substring(0, ratings.length() - 1) : "");
            orderEntity.setReview(!reviews.isEmpty() ? reviews.substring(0, reviews.length() - 1) : "");
            orderEntity.setTimeStamp(!timeStamp.isEmpty() ? timeStamp.substring(0, timeStamp.length() - 1) : "");
            orderEntity.setNameGuest(!timeStamp.isEmpty() ? name.substring(0, name.length() - 1) : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderEntity;
    }

    @Override
    public HostEntity updateHostRating(String hostId,String geoHost, double userRating) {
        
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":hostId", new AttributeValue().withS(hostId));

        DynamoDBQueryExpression<HostEntity> queryExpression = new DynamoDBQueryExpression<HostEntity>()
                .withKeyConditionExpression("pk = :hostId")
                .withExpressionAttributeValues(eav)
                .withProjectionExpression("ratH,noOfRat"); 

        
        PaginatedQueryList<HostEntity> result = dynamoDBMapper.query(HostEntity.class, queryExpression);

        
        HostEntity hostEntity = result.get(0); 

        
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

        
        hostEntity.setRatingHost(String.valueOf(newAverageRating));
        hostEntity.setNoOfRating(String.valueOf(numberOfRatings));


        Map<String, String> attributeUpdates = new HashMap<>();
        attributeUpdates.put("ratH", String.valueOf(newAverageRating));
        attributeUpdates.put("noOfRat", String.valueOf(numberOfRatings));

        commonMethods.updateMultipleAttributes(hostId,geoHost, attributeUpdates);

        return hostEntity;
    }

}






































































