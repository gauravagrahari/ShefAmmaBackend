package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.Repository.OrderTrack;
import com.shefamma.shefamma.entities.OrderTrackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class OrderTrackController {
    @Autowired
    OrderTrack orderTrack;

    @GetMapping("/admin/getAllMealOrderTrack")
    public List<OrderTrackEntity> getAllMealOrderTrack(@RequestParam String mealType) {
        return orderTrack.getAllMealOrderTracks(mealType);
    }
    @GetMapping("/admin/getOrderTrackByMealTypeAndDateRange")
    public ResponseEntity<List<OrderTrackEntity>> getOrderTrackByMealTypeAndDateRange(
            @RequestParam String mealType,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<OrderTrackEntity> orderTracks =  orderTrack.getOrderTrackByMealTypeAndDateRange(mealType, startDate, endDate);
        if (orderTracks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderTracks);
    }
    @GetMapping("/orderTrackDetail")
    public ResponseEntity<?> getOrderTrackDetail(
            @RequestParam String mealType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String hostUuid) {

        Integer orderDetail = orderTrack.getOrderDetailForHost(mealType, startDate, endDate, hostUuid);
        if (orderDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.singletonMap("orderCount", orderDetail));
    }


}
