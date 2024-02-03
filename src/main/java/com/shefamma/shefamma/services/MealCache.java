package com.shefamma.shefamma.services;

import com.shefamma.shefamma.entities.MealEntity;

import java.util.concurrent.ConcurrentHashMap;
public class MealCache {
    private static final ConcurrentHashMap<String, MealEntity> mealCache = new ConcurrentHashMap<>();

    // Method to generate a composite key
    private static String generateCompositeKey(String uuidMeal, String nameItem) {
        return uuidMeal + "#" + nameItem;
    }

    public static MealEntity getMeal(String uuidMeal, String nameItem) {
        String key = generateCompositeKey(uuidMeal, nameItem);
        return mealCache.get(key);
    }

    public static void putMeal(String uuidMeal, String nameItem, MealEntity meal) {
        String key = generateCompositeKey(uuidMeal, nameItem);
        mealCache.put(key, meal);
    }

    public static void removeMeal(String uuidMeal, String nameItem) {
        String key = generateCompositeKey(uuidMeal, nameItem);
        mealCache.remove(key);
    }
}

