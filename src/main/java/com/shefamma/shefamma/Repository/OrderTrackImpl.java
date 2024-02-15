package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.OrderTrackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderTrackImpl implements OrderTrack{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    public List<OrderTrackEntity> getAllMealOrderTracks(String mealType) {
        OrderTrackEntity gsiKeyCondition = new OrderTrackEntity();
        gsiKeyCondition.setPk("orderTrack#" + mealType);

        DynamoDBQueryExpression<OrderTrackEntity> queryExpression = new DynamoDBQueryExpression<OrderTrackEntity>()
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false); // Adjust based on your consistency requirements

        return dynamoDBMapper.query(OrderTrackEntity.class, queryExpression);
    }
    @Autowired
    public List<OrderTrackEntity> getOrderTrackByMealTypeAndDateRange(String mealType, String startTime, String endTime) {
        // Construct the partition key value based on meal type
        String partitionKeyValue = "orderTrack#" + mealType;

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":pk", new AttributeValue().withS(partitionKeyValue));
        eav.put(":startTime", new AttributeValue().withS(startTime));
        eav.put(":endTime", new AttributeValue().withS(endTime));

        DynamoDBQueryExpression<OrderTrackEntity> queryExpression = new DynamoDBQueryExpression<OrderTrackEntity>()
                .withKeyConditionExpression("pk = :pk and sk BETWEEN :startTime AND :endTime")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        return dynamoDBMapper.query(OrderTrackEntity.class, queryExpression);
    }
    @Autowired
    public Integer getOrderDetailForHost(String mealType, String startDate, String endDate, String hostUuid) {
        // Fetch order tracks for a given meal type within the specified date range
        List<OrderTrackEntity> orderTracks = getOrderTrackByMealTypeAndDateRange(mealType, startDate, endDate);

        // Loop through each fetched order track
        for (OrderTrackEntity track : orderTracks) {
            // Check if the track contains the specific host UUID in its hostOrders map
            if (track.getHostOrders() != null && track.getHostOrders().containsKey(hostUuid)) {
                // If found, return the order count for this host UUID
                return track.getHostOrders().get(hostUuid);
            }
        }
        // If no matching host UUID found in any of the tracks, return null or consider throwing a custom exception
        return null;
    }



}
