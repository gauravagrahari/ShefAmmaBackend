package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.MealEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Meal {
    ResponseEntity<MealEntity> createMeal(MealEntity mealEntity);
    MealEntity updateMealAttribute(String partition, String sort, String attributeName, MealEntity itemEntity);
    List<MealEntity> getItems(String itemId);
    MealEntity getItem(String hostId, String nameItem, MealEntity itementity);


    MealEntity getOneMeal(String hostId, String nameItem);
}


