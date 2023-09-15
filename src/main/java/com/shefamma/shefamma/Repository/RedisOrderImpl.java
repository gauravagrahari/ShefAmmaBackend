package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedisOrderImpl {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
//    @Autowired
//    private RedisTemplate<String, OrderEntity> redisTemplate;
    public void saveOrderToAppropriateList(OrderEntity order) {
        String mealTypeKey;
        switch (order.getMealType()) {
            case "b":
                mealTypeKey = "breakfastOrders";
                break;
            case "l":
                mealTypeKey = "lunchOrders";
                break;
            case "d":
                mealTypeKey = "dinnerOrders";
                break;
            default:
                throw new IllegalArgumentException("Invalid meal type");
        }
        redisTemplate.opsForList().rightPush(mealTypeKey, order);
    }
    public List<OrderEntity> getOrdersByMealType(String mealType) {
        String mealTypeKey;

        switch (mealType) {
            case "b":
                mealTypeKey = "breakfastOrders";
                break;
            case "l":
                mealTypeKey = "lunchOrders";
                break;
            case "d":
                mealTypeKey = "dinnerOrders";
                break;
            default:
                throw new IllegalArgumentException("Invalid meal type");
        }

        List<Object> objects = redisTemplate.opsForList().range(mealTypeKey, 0, -1);
        List<OrderEntity> orders = objects.stream()
                .map(object -> (OrderEntity) object)
                .collect(Collectors.toList());

        return orders;
    }

}
