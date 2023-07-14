package com.shefamma.shefamma.HostRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private Host host;


    @Override
    public OrderEntity createOrder(OrderEntity orderEntity) {
        orderEntity.setUuidOrder("order#"+orderEntity.getUuidOrder());
        dynamoDBMapper.save(orderEntity);

//        // Update the capacity of the specific SlotSubEntity
//        String[] orderPk =orderEntity.getHostId().split("#");
//        String timeSlotpk="time#"+orderPk[1];
//
//        // create a new class entity to recieve order entiy from
//        String uniqueAttrValue= String.valueOf(orderEntity.getStartTime());
//        String noOfGuests= String.valueOf(orderEntity.getNoOfGuest());
//
//        //this method has got mistakes
//        commonMethods.updateTimeSlotCapOrderEntity("pk", timeSlotpk,"slots","capacity",noOfGuests,"1");
////        commonMethods.updateTimeSlotCapOrderEntity("pk", timeSlotpk,"slots","capacity","startTime",uniqueAttrValue,Integer.parseInt(noOfGuests),"1");
////        commonMethods.updateTimeSlotCapOrderEntity("pk", timeSlotpk,"slots","capacity","startTime",uniqueAttrValue, Integer.parseInt(noOfGuests),"1");
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
    public OrderEntity updateOrder(String partition, String sort, String attributeName, OrderEntity orderEntity) {
        String value = null;
//         Get the value of the specified attribute
        switch (attributeName) {
            case "status" -> value = orderEntity.getStatus();
            case "review" -> value = orderEntity.getReview();
            case "rating" -> {
                value = orderEntity.getRating();
                HostEntity hostEntity=host.updateHostRating(orderEntity.getHostId(), Double.parseDouble(value));
                System.out.println(hostEntity);
            }
            case "payment" -> value =orderEntity.getPayMode();

            default ->
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

    @Override
    public void updatePayment(OrderEntity orderEntity) {

        Map<String, String> attributeUpdates = new HashMap<>();
        attributeUpdates.put("pyMd", orderEntity.getPayMode());
        attributeUpdates.put("status",orderEntity.getStatus());

        commonMethods.updateMultipleAttributes(orderEntity.getUuidOrder(),orderEntity.getTimeStamp(), attributeUpdates);
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
