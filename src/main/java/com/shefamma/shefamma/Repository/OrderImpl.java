package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderImpl implements Order{

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
    @Autowired
    private RedisOrderImpl redisOrderImpl;

//    @Override
//    public OrderEntity createOrder(OrderEntity orderEntity) {
//        orderEntity.setUuidOrder("order#"+orderEntity.getUuidOrder());
//        dynamoDBMapper.save(orderEntity);
//        return orderEntity;
////        // Update the capacity of the specific SlotSubEntity
////        String[] orderPk =orderEntity.getHostId().split("#");
////        String timeSlotpk="time#"+orderPk[1];
////
////        // create a new class entity to recieve order entiy from
////        String uniqueAttrValue= String.valueOf(orderEntity.getStartTime());
////        String noOfGuests= String.valueOf(orderEntity.getNoOfGuest());
////
////        //this method has got mistakes
////        commonMethods.updateTimeSlotCapOrderEntity("pk", timeSlotpk,"slots","capacity",noOfGuests,"1");
//////        commonMethods.updateTimeSlotCapOrderEntity("pk", timeSlotpk,"slots","capacity","startTime",uniqueAttrValue,Integer.parseInt(noOfGuests),"1");
//////        commonMethods.updateTimeSlotCapOrderEntity("pk", timeSlotpk,"slots","capacity","startTime",uniqueAttrValue, Integer.parseInt(noOfGuests),"1");
//
//    }
@Override
public OrderEntity createOrder(OrderEntity orderEntity) {
    // Split the uuidOrder and replace "guest" with "order"
    String[] parts = orderEntity.getUuidOrder().split("#");
    if (parts.length != 2 || !parts[0].equalsIgnoreCase("guest")) {
        // Handle invalid uuidOrder format
        throw new IllegalArgumentException("Invalid uuidOrder format");
    }

    String modifiedUuidOrder = "order#" + parts[1];
    orderEntity.setUuidOrder(modifiedUuidOrder);

    dynamoDBMapper.save(orderEntity);

    // Save the order to the appropriate Redis list based on mealType
    redisOrderImpl.saveOrderToAppropriateList(orderEntity);
    return orderEntity;
}

    public List<OrderEntity> getHostOrders(String uuidOrder) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        gsiKeyCondition.setUuidHost(uuidOrder); // Assuming "gsi1pk" is the attribute for the GSI's PK

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName("gsi1") // Replace "gsi1" with the actual GSI name
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }
    public List<OrderEntity> getInProgressHostOrders(String uuidOrder) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        gsiKeyCondition.setUuidHost(uuidOrder); // Assuming "gsi1pk" is the attribute for the GSI's PK

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName("gsi1") // Replace "gsi1" with the actual GSI name
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        // Add a condition to fetch items where 'status' attribute is equal to "ip"
        Map<String, Condition> queryFilter = new HashMap<>();
        queryFilter.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS("ip")));
        queryExpression.withQueryFilter(queryFilter);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }
    public List<OrderEntity> getInProgressDevBoyOrders(String uuidDevBoy) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        gsiKeyCondition.setUuidDevBoy(uuidDevBoy); // Assuming "gsi1pk" is the attribute for the GSI's PK
//        gsiKeyCondition.setUuidDevBoy(uuidOrder); // Assuming "gsi1pk" is the attribute for the GSI's PK

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName("gsi2") // Replace "gsi1" with the actual GSI name
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        // Add a condition to fetch items where 'status' attribute is equal to "ip"
        Map<String, Condition> queryFilter = new HashMap<>();
        queryFilter.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS("ip")));
        queryExpression.withQueryFilter(queryFilter);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }
    // Helper method to map the DynamoDB item to your OrderEntity class
//    private OrderEntity mapDynamoDBItemToOrderEntity(Map<String, AttributeValue> item) {
//        // Implement the mapping logic to convert the DynamoDB item to OrderEntity
//        // For example:
//        String uuidOrder = item.get("uuidOrder").getS();
//        String timeStamp = item.get("timeStamp").getS();
//        // ... (Map other attributes)
//        // Return an instance of OrderEntity
//        return new OrderEntity(uuidOrder, timeStamp, /* Other attributes */);
//    }


    @Override
    public List<OrderEntity> getGuestOrders(String uuidOrder) {
        // Split the uuidOrder and use 'order' instead of the first part
        String[] parts = uuidOrder.split("#");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("guest")) {
            // Handle invalid uuidOrder format
            throw new IllegalArgumentException("Invalid uuidOrder format");
        }

        String modifiedUuidOrder = "order#" + parts[1];

        OrderEntity keyCondition = new OrderEntity();
        keyCondition.setUuidOrder(modifiedUuidOrder);

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withHashKeyValues(keyCondition)
                .withConsistentRead(false);

        List<OrderEntity> result = dynamoDBMapper.query(OrderEntity.class, queryExpression);

        for (OrderEntity order : result) {
            System.out.println(order);
        }

        return result;
    }
    @Override
//    public void cancelOrder(String partition, String sort, String attributeName, String status) {
    public OrderEntity updateOrder(String partition, String sort, String attributeName, OrderEntity orderEntity) {
        String value = null;
//         Get the value of the specified attribute
        switch (attributeName) {
            case "status" -> value = orderEntity.getStatus();
            case "review" ->{
                attributeName="rev";
                value = orderEntity.getReview();}
            case "rating" -> {
                value = orderEntity.getRating();
                attributeName="rat";

                HostEntity hostEntity=host.updateHostRating(orderEntity.getUuidHost(),orderEntity.getGeoHost(), Double.parseDouble(value));
                System.out.println(hostEntity);
            }
            case "payment" -> value =orderEntity.getPayMode();
            case "uuidDevBoy" -> value =orderEntity.getUuidDevBoy();

            default ->
                // Invalid attribute name provided
                    throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        commonMethods.updateAttributeWithSortKey(partition,sort,attributeName,value);
        return orderEntity;
    }
    @Override
//    public void cancelOrder(String partition, String sort, String attributeName, String status) {
    public OrderEntity updateOrderDevBoyUuid(String partition, String sort, String attributeName, String uuidDevBoy) {
        commonMethods.updateAttributeWithSortKey(partition,sort,attributeName,uuidDevBoy);
        return orderEntity;
    }
    //not required anymore, this will be handled by updateOrder
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
//see, I have already set up or tools and you helped me in its testing-----Now I will tell you my requirement again---there will be multiple orders, each order
//        will have almost unique destinations(customers), but there's high chance that the source(cooks) of many orders might be same, now what will happen that the orders will be delivered in 3 different time slots(each slot would be for each meal , i.e breakfast, lunch and dinner)-----
//        before each time slots start time, the orders will be assigned to devBoys,
//        and also all will be  orders taken 2 hour before the time slots start time(so that the cooks have enough time to prepare and pack the food). For a gist, we will no all the orders before the delivery time, there geocodes will also  be knowsn, the only thing that we won't be nowing
//        that which delivery boy will carry which order for delivery. ---------So now I want you to form the best and most optimized approach and sequence in which the order will be assigned to the devBoys.