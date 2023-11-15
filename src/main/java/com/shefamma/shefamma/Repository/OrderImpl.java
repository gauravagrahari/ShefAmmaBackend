package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import com.shefamma.shefamma.entities.OrderWithAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

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
public OrderEntity  createOrder(OrderEntity orderEntity) {
    // Split the uuidOrder and replace "guest" with "order"
    String[] parts = orderEntity.getUuidOrder().split("#");
    if (parts.length != 2 || !parts[0].equalsIgnoreCase("guest")) {
        // Handle invalid uuidOrder format
        throw new IllegalArgumentException("Invalid uuidOrder format");
    }

    String modifiedUuidOrder = "order#" + parts[1];
    orderEntity.setUuidOrder(modifiedUuidOrder);
    dynamoDBMapper.save(orderEntity);
  System.out.println(orderEntity);
    // Save the order to the appropriate Redis list based on mealType
    redisOrderImpl.saveOrderToAppropriateList(orderEntity);
    return orderEntity;
}

    public List<OrderEntity> getAllOrders(String uuidOrder,String gsiName) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        if(Objects.equals(gsiName, "gsi1")){
        gsiKeyCondition.setUuidHost(uuidOrder); }// Assuming "gsi1pk" is the attribute for the GSI's PK
else{
            gsiKeyCondition.setUuidDevBoy(uuidOrder);
        }
        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName) // Replace "gsi1" with the actual GSI name
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }
    public List<OrderEntity> getInProgressOrders(String uuidOrder, String gsiName) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        gsiKeyCondition.setUuidHost(uuidOrder); // Assuming "gsi1pk" is the attribute for the GSI's PK

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName) // Replace "gsi1" with the actual GSI name
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        // Add a condition to fetch items where 'status' attribute is equal to "ip"
        Map<String, Condition> queryFilter = new HashMap<>();
        queryFilter.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS("ip")));
        queryExpression.withQueryFilter(queryFilter);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }
    public List<OrderEntity> getInProgressAndPkdOrders(String uuidOrder, String gsiName) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        gsiKeyCondition.setUuidHost(uuidOrder);

        DynamoDBQueryExpression<OrderEntity> queryExpressionForIp = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName)
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        // Add a condition to fetch items where 'status' attribute is equal to "ip"
        Map<String, Condition> queryFilterForIp = new HashMap<>();
        queryFilterForIp.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS("ip")));
        queryExpressionForIp.withQueryFilter(queryFilterForIp);

        List<OrderEntity> ipOrders = dynamoDBMapper.query(OrderEntity.class, queryExpressionForIp);

        DynamoDBQueryExpression<OrderEntity> queryExpressionForPkd = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName)
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        // Add a condition to fetch items where 'status' attribute is equal to "pkd"
        Map<String, Condition> queryFilterForPkd = new HashMap<>();
        queryFilterForPkd.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS("pkd")));
        queryExpressionForPkd.withQueryFilter(queryFilterForPkd);

        List<OrderEntity> pkdOrders = dynamoDBMapper.query(OrderEntity.class, queryExpressionForPkd);

        ipOrders.addAll(pkdOrders);

        return ipOrders;
    }
    public List<OrderWithAddress> getInProgressDevBoyOrders(String uuidDevBoy) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        gsiKeyCondition.setUuidDevBoy(uuidDevBoy);

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":statusVal1", new AttributeValue().withS("ip"));
        expressionAttributeValues.put(":statusVal2", new AttributeValue().withS("pkd"));

        DynamoDBQueryExpression<OrderEntity> orderQueryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName("gsi2")
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false)
                .withFilterExpression("stts = :statusVal1 OR stts = :statusVal2")
                .withExpressionAttributeValues(expressionAttributeValues);

        List<OrderEntity> orders = dynamoDBMapper.query(OrderEntity.class, orderQueryExpression);

        List<OrderWithAddress> detailedOrders = new ArrayList<>();

        for (OrderEntity order : orders) {
            // Query for Host address
            DynamoDBQueryExpression<HostEntity> hostQueryExpression = new DynamoDBQueryExpression<HostEntity>()
                    .withHashKeyValues(new HostEntity().setUuidHost(order.getUuidHost()))
                    .withProjectionExpression("adr");

            List<HostEntity> hostResults = dynamoDBMapper.query(HostEntity.class, hostQueryExpression);
            String uuidGuest = order.getUuidOrder().replaceFirst("order#", "guest#");

            // Query for Guest address
            DynamoDBQueryExpression<GuestEntity> guestQueryExpression = new DynamoDBQueryExpression<GuestEntity>()
                    .withHashKeyValues(new GuestEntity().setUuidGuest(uuidGuest))
                    .withProjectionExpression("adr");

            List<GuestEntity> guestResults = dynamoDBMapper.query(GuestEntity.class, guestQueryExpression);

            OrderWithAddress orderDetail = new OrderWithAddress();
            orderDetail.setOrder(order);

            if (!hostResults.isEmpty()) {
                orderDetail.setHostAddress(hostResults.get(0).getAddressHost());
            }

            if (!guestResults.isEmpty()) {
                orderDetail.setGuestAddress(guestResults.get(0).getAddressGuest());
            }

            detailedOrders.add(orderDetail);
        }

        return detailedOrders;
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
    public OrderEntity updateOrder(String partition, String sort, String attributeName, OrderEntity orderEntity) {
        String value = null;
//         Get the value of the specified attribute
        switch (attributeName) {
            case "status" ->{ value = orderEntity.getStatus();
                attributeName="stts";
            }
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
            case "uuidDevBoy" ->{
                attributeName="gpk2";
                value =orderEntity.getUuidDevBoy();}
            case "pickUpTime" ->{
                attributeName="pTime";
                value = orderEntity.getPickUpTime();}
            case "deliverTime" -> {
                value = orderEntity.getDeliverTime();
                attributeName="dTime";
            }
            case "cancelledTime" -> {
                value = orderEntity.getCancelledTime();
                attributeName="cTime";
            }
            default ->
                // Invalid attribute name provided
                    throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        UpdateItemResult result = commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);

        // Check if the update was successful
        if (result != null && result.getAttributes() != null && !result.getAttributes().isEmpty()) {
            return orderEntity;
        } else {
            throw new RuntimeException("Failed to update order"); // Or handle this error in another way
        }
    }
    @Override
    public OrderEntity updateOrderStatus(String uuidOrder, String timeStamp, String attributeName, String attributeName2, OrderEntity orderEntity) {
        String value = null;
        String value2 = null;

        if(attributeName=="status"){
            value = orderEntity.getStatus();
            attributeName="stts";
        }
//         Get the value of the specified attribute
        switch (attributeName2) {
            case "pickUpTime" ->{
                attributeName2="pTime";
                value2 = orderEntity.getPickUpTime();}
            case "deliverTime" -> {
                value2 = orderEntity.getDeliverTime();
                attributeName2="dTime";
            }
            default ->
                // Invalid attribute name provided
                    throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        commonMethods.updateTwoAttributesWithSortKey(uuidOrder,timeStamp,attributeName,value,attributeName2,value2);
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