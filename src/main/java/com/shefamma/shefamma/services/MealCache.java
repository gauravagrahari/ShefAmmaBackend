package com.shefamma.shefamma.services;

import com.shefamma.shefamma.entities.MealEntity;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MealCache {
    private static final ConcurrentHashMap<String, MealEntity> mealCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Set<String>> uuidToMealKeys = new ConcurrentHashMap<>();

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
        Set<String> keys = uuidToMealKeys.get(uuidMeal);
        if (keys != null) {
            keys.remove(key);
            if (keys.isEmpty()) {
                uuidToMealKeys.remove(uuidMeal);
            }
        }
    }

    public static Set<MealEntity> getMealsByUuid(String uuidMeal) {
        Set<String> keys = uuidToMealKeys.getOrDefault(uuidMeal, Collections.emptySet());
        return keys.stream().map(mealCache::get).filter(meal -> meal != null).collect(Collectors.toSet());
    }
}

