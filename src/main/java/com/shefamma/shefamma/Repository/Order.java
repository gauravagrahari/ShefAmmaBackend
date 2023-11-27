package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.OrderEntity;
import com.shefamma.shefamma.entities.OrderWithAddress;

import java.util.List;

public interface Order {
    List<OrderEntity> getAllOrders(String hostID,String gsiName);
    OrderEntity createOrder(OrderEntity orderEntity);


    List<OrderEntity> getInProgressOrders(String hostID, String gsiName);
    List<OrderWithAddress> getInProgressDevBoyOrders(String uuidDevBoy);

    List<OrderEntity> getGuestOrders(String uuidOrder);

    OrderEntity updateOrder(String partition, String sort, String attributeName, OrderEntity orderEntity);
    OrderEntity updateOrderDevBoyUuid(String partition, String sort, String attributeName, String devBoyUuid);

    OrderEntity cancelOrder(OrderEntity orderEntity);

    void updatePayment(OrderEntity orderEntity);

    OrderEntity updateOrderStatus(String uuidOrder, String timeStamp, String attributeName, String attributeName2, OrderEntity orderEntity);

    List<OrderEntity> getInProgressAndPkdOrders(String uuidDevBoy, String gsi2);

    List<OrderEntity> getOrdersByStatus(String id, String gsiName, String status);

    List<OrderEntity> getAllOrdersByStatus(List<String> ids, String gsiName, String status);
}
