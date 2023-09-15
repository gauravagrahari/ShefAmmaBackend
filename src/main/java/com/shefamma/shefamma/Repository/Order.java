package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.OrderEntity;

import java.util.List;

public interface Order {
    OrderEntity createOrder(OrderEntity orderEntity);

    List<OrderEntity> getHostOrders(String hostID);
    List<OrderEntity> getInProgressHostOrders(String hostID);
    List<OrderEntity> getInProgressDevBoyOrders(String uuidDevBoy);

    List<OrderEntity> getGuestOrders(String uuidOrder);

    OrderEntity updateOrder(String partition, String sort, String attributeName, OrderEntity orderEntity);
    OrderEntity updateOrderDevBoyUuid(String partition, String sort, String attributeName, String devBoyUuid);

    OrderEntity cancelOrder(OrderEntity orderEntity);

    void updatePayment(OrderEntity orderEntity);
}
