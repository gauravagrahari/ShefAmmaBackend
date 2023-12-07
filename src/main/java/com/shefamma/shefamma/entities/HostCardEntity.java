package com.shefamma.shefamma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostCardEntity {
    HostEntity hostEntity;
    @Getter
    private List<MealEntity> meals;
    public void setMeals(List<MealEntity> meals) {
        this.meals = meals;
    }



//    List<String> itemNames;
//    List<String> mealTypes; // Add List<String> for mealTypes
//    List<String> imageMeal; // Add List<String> for mealTypes
//
//    public List<String> getItemNames() {
//        return itemNames;
//    }
//    public void setItemNames(List<String> itemNames) {
//        this.itemNames = itemNames;
//    }
//    public List<String> getMealTypes() {
//        return mealTypes;
//    }
//    public void setMealTypes(List<String> mealTypes) {
//        this.mealTypes = mealTypes;
//    }
//    public List<String> getImageMeal() {
//        return imageMeal;
//    }
//    public void setImageMeal(List<String> imageMeal) {
//        this.imageMeal = imageMeal;
//    }

}
