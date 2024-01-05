package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.OrderTrackEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderTrack {
    List<OrderTrackEntity> getAllMealOrderTracks(String mealType);
}
