package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import com.shefamma.shefamma.entities.OrderWithAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderImpl implements Order{

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private CommonMethods commonMethods;
    @Autowired
    private Host host;

    Logger logger = LoggerFactory.getLogger(getClass());


@Override
public OrderEntity  createOrder(OrderEntity orderEntity) {
    
    String[] parts = orderEntity.getUuidOrder().split("#");
    if (parts.length != 2 || !parts[0].equalsIgnoreCase("guest")) {
        
        throw new IllegalArgumentException("Invalid uuidOrder format");
    }

    String modifiedUuidOrder = "order#" + parts[1];
    orderEntity.setUuidOrder(modifiedUuidOrder);
    dynamoDBMapper.save(orderEntity);


    return orderEntity;
}

    public List<OrderEntity> getAllOrders(String uuidOrder,String gsiName) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        if(Objects.equals(gsiName, "gsi1")){
        gsiKeyCondition.setUuidHost(uuidOrder); }
        else{
            gsiKeyCondition.setUuidDevBoy(uuidOrder);
        }
        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName) 
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }


    public List<OrderEntity> getInProgressOrders(String uuidOrder, String gsiName) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        gsiKeyCondition.setUuidHost(uuidOrder); 

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName) 
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        
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

        
        Map<String, Condition> queryFilterForIp = new HashMap<>();
        queryFilterForIp.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS("ip")));
        queryExpressionForIp.withQueryFilter(queryFilterForIp);

        List<OrderEntity> ipOrders = dynamoDBMapper.query(OrderEntity.class, queryExpressionForIp);

        DynamoDBQueryExpression<OrderEntity> queryExpressionForPkd = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName)
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);

        
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
            
            DynamoDBQueryExpression<HostEntity> hostQueryExpression = new DynamoDBQueryExpression<HostEntity>()
                    .withHashKeyValues(new HostEntity().setUuidHost(order.getUuidHost()))
                    .withProjectionExpression("adr");

            List<HostEntity> hostResults = dynamoDBMapper.query(HostEntity.class, hostQueryExpression);
            String uuidGuest = order.getUuidOrder().replaceFirst("order#", "guest#");

            
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
    public List<OrderEntity> getOrdersByStatus(String id, String gsiName, String status) {
        OrderEntity gsiKeyCondition = new OrderEntity();
        if(Objects.equals(gsiName, "gsi1")){
            gsiKeyCondition.setUuidHost(id); }
        else{
            gsiKeyCondition.setUuidDevBoy(id);
        }

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withIndexName(gsiName)
                .withHashKeyValues(gsiKeyCondition)
                .withConsistentRead(false);


        Map<String, Condition> queryFilter = new HashMap<>();
        queryFilter.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(status)));
        queryExpression.withQueryFilter(queryFilter);

        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }
    public List<OrderEntity> getAllOrdersByStatus(List<String> ids, String gsiName, String status) {
        List<OrderEntity> allOrders = new ArrayList<>();

        for (String id : ids) {
            OrderEntity gsiKeyCondition = new OrderEntity();
            if(Objects.equals(gsiName, "gsi1")) {
                gsiKeyCondition.setUuidHost(id);
            } else {
                gsiKeyCondition.setUuidDevBoy(id);
            }

            DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                    .withIndexName(gsiName)
                    .withHashKeyValues(gsiKeyCondition)
                    .withConsistentRead(false);

            Map<String, Condition> queryFilter = new HashMap<>();
            queryFilter.put("stts", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(status)));
            queryExpression.withQueryFilter(queryFilter);

            List<OrderEntity> ordersForId = dynamoDBMapper.query(OrderEntity.class, queryExpression);

            allOrders.addAll(ordersForId);
        }


        return allOrders;
    }

    @Override
    public List<OrderEntity> getGuestOrders(String uuidOrder) {
        
        String[] parts = uuidOrder.split("#");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("guest")) {
            
            throw new IllegalArgumentException("Invalid uuidOrder format");
        }

        String modifiedUuidOrder = "order#" + parts[1];

        OrderEntity keyCondition = new OrderEntity();
        keyCondition.setUuidOrder(modifiedUuidOrder);

        DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                .withHashKeyValues(keyCondition)
                .withConsistentRead(false);


        return dynamoDBMapper.query(OrderEntity.class, queryExpression);
    }
    @Override
    public OrderEntity updateOrder(String partition, String sort, String attributeName, OrderEntity orderEntity) {
        String value;

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

               host.updateHostRating(orderEntity.getUuidHost(),orderEntity.getGeoHost(), Double.parseDouble(value));

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
                
                    throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        UpdateItemResult result = commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);

        
        if (result != null && result.getAttributes() != null && !result.getAttributes().isEmpty()) {
            return orderEntity;
        } else {
            throw new RuntimeException("Failed to update order"); 
        }
    }

    @Override
    public void updatePayment(OrderEntity orderEntity) {

        Map<String, String> attributeUpdates = new HashMap<>();
        attributeUpdates.put("pyMd", orderEntity.getPayMode());
        attributeUpdates.put("status",orderEntity.getStatus());

        commonMethods.updateMultipleAttributes(orderEntity.getUuidOrder(),orderEntity.getTimeStamp(), attributeUpdates);
    }

    @Override
    public OrderEntity updateOrderStatus(String uuidOrder, String timeStamp, String attributeName, String attributeName2, OrderEntity orderEntity) {
        return null;
    }

    @Override
    public List<OrderEntity> getOrdersBetweenTimestamps(String gsiName, String startTimestamp, String endTimestamp) {
        String keyConditionExpression = "gpk = :gpk AND gsk BETWEEN :startTs AND :endTs";
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":gpk", new AttributeValue().withS("gpkValue")); 
        eav.put(":startTs", new AttributeValue().withS(startTimestamp));
        eav.put(":endTs", new AttributeValue().withS(endTimestamp));

        return executeQueryForOrders(gsiName, keyConditionExpression, eav);
    }
    @Override
    public List<OrderEntity> getOrdersBetweenTimestampsForMultipleIds(List<String> ids, String gsiName, String startTimestamp, String endTimestamp) {
        List<OrderEntity> allOrders = new ArrayList<>();
        for (String id : ids) {
            String keyConditionExpression = "gpk = :gpk AND gsk BETWEEN :startTs AND :endTs";
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":gpk", new AttributeValue().withS(id));
            eav.put(":startTs", new AttributeValue().withS(startTimestamp));
            eav.put(":endTs", new AttributeValue().withS(endTimestamp));

            allOrders.addAll(executeQueryForOrders(gsiName, keyConditionExpression, eav));
        }
        return allOrders;
    }

    private List<OrderEntity> executeQueryForOrders(String gsiName, String keyConditionExpression, Map<String, AttributeValue> expressionAttributeValues) {
        try {
            
            DynamoDBQueryExpression<OrderEntity> queryExpression = new DynamoDBQueryExpression<OrderEntity>()
                    .withIndexName(gsiName)
                    .withKeyConditionExpression(keyConditionExpression)
                    .withExpressionAttributeValues(expressionAttributeValues)
                    .withConsistentRead(false);

            
            return dynamoDBMapper.query(OrderEntity.class, queryExpression);
        } catch (Exception e) {
            logger.error("Error executing query for orders in DynamoDB", e);
            throw new RuntimeException("Error executing query for orders in DynamoDB", e);
        }
    }

}




