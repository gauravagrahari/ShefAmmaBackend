package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderiImpl implements Order{

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private OrderEntity orderEntity;
    @Autowired
    private GuestEntity guestEntity;

    @Autowired
    private CommonMethods commonMethods;


    @Override
    public OrderEntity createOrder(OrderEntity orderEntity) {
        orderEntity.setUuidOrder(orderEntity.getUuidOrder() + "#order");
        dynamoDBMapper.save(orderEntity);

        // Update the capacity of the specific SlotSubEntity
        String orderPk[]=orderEntity.getUuidOrder().split("#");
        String timeSlotpk=orderPk[0]+"#time";

        // create a new class entity to recieve order entiy from
        String uniqueAttrValue= String.valueOf(orderEntity.getStartTime());
        String noOfGuests= String.valueOf(orderEntity.getNoOfGuest());

        commonMethods.updateSpecificAttributeOrderEntity("pk", timeSlotpk,"slots","capacity","startTime",uniqueAttrValue,noOfGuests);
        return orderEntity;
    }

    @Override
    public List<OrderEntity> getHostOrders(OrderEntity orderEntity) {
//        orderEntity.
        return null;
    }


    @Override
    public List<OrderEntity> getGuestOrders(OrderEntity orderEntity) {
        String guestId = orderEntity.getUuidOrder();

        OrderEntity keyCondition = new OrderEntity();
        keyCondition.setUuidOrder(guestId);
        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withHashKeyValues(keyCondition)
                .withConsistentRead(false);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }

    @Override
//    public void cancelOrder(String partition, String sort, String attributeName, String status) {
    public OrderEntity cancelOrder(String partition, String sort, String attributeName, OrderEntity orderEntity) {
        String value = null;
//         Get the value of the specified attribute
        switch (attributeName) {
            case "status":
                value = orderEntity.getStatus();
                break;
            case "review":
                value = orderEntity.getReview();
                break;
            case "rating":
                value=orderEntity.getRating();
                break;
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        commonMethods.updateAttributeWithSortKey(partition,sort,attributeName,value);
        return orderEntity;
    }
    @Override
    public OrderEntity cancelOrder(OrderEntity orderEntity) {
        String partition=orderEntity.getUuidOrder();
        String sort=orderEntity.getTimeStamp();
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
                .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)))
                .withExpectedEntry("sk", new ExpectedAttributeValue(new AttributeValue(sort)));

        dynamoDBMapper.save(orderEntity, saveExpression);
        return orderEntity;
    }
}

//public GuestEntity updateGuest(String partition, String sort, GuestEntity guestEntity) {
//    DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
//            .withExpectedEntry("pk", new ExpectedAttributeValue(new AttributeValue(partition)))
//            .withExpectedEntry("sk", new ExpectedAttributeValue(new AttributeValue(sort)));
//
//    dynamoDBMapper.save(guestEntity, saveExpression);
//
//    return guestEntity;
//}
