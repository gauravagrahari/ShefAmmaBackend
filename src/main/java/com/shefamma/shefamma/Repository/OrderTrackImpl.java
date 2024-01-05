package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.shefamma.shefamma.entities.OrderTrackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
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


}
