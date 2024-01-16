package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.CapacityEntity;
import org.springframework.http.ResponseEntity;

public interface Capacity {
    CapacityEntity createCapacity(CapacityEntity capacityentity);

    CapacityEntity getCapacity(String s);

    String getCurrentCapacity(String mealType,  String partition);
    ResponseEntity<String> updateCapacity(String mealType,   String partition, int noOfMeals);


    ResponseEntity<String> increaseCapacity(String mealType, String partition, int noOfMeals);
}
