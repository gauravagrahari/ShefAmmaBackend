package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.Repository.OrderTrack;
import com.shefamma.shefamma.entities.OrderTrackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderTrackController {
    @Autowired
    OrderTrack orderTrack;

//    @GetMapping("/admin/getOrderTrack")
//    public ResponseEntity<OrderTrackEntity> getCharges() {
//        return orderTrack.getAllOrderTrack();
//    }
    @GetMapping("/admin/getAllMealOrderTrack")
    public List<OrderTrackEntity> getAllMealOrderTrack(@RequestParam String mealType) {
        return orderTrack.getAllMealOrderTracks(mealType);
    }

}
