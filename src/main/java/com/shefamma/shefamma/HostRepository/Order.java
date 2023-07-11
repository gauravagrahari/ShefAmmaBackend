package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.OrderEntity;

import java.util.List;

public interface Order {
    OrderEntity createOrder(OrderEntity orderEntity);

    List<OrderEntity> getHostOrders(OrderEntity orderEntity);

    List<OrderEntity> getGuestOrders(OrderEntity orderEntity);

    OrderEntity updateOrder(String partition, String sort, String attributeName, OrderEntity orderEntity);

    OrderEntity cancelOrder(OrderEntity orderEntity);

}
