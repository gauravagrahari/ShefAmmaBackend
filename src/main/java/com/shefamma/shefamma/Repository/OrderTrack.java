package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.OrderTrackEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderTrack {
    List<OrderTrackEntity> getAllMealOrderTracks(String mealType);
    List<OrderTrackEntity> getOrderTrackByMealTypeAndDateRange(String mealType, String startTime, String endTime);
    Integer getOrderDetailForHost(String mealType, String startDate, String endDate, String hostUuid);
}
