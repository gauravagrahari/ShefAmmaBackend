package com.shefamma.shefamma.services;

import com.shefamma.shefamma.entities.ConstantChargesEntity;
import com.shefamma.shefamma.entities.MealEntity;

import java.util.concurrent.ConcurrentHashMap;

public class CacheUtility {
    private static final ConcurrentHashMap<String, MealEntity> mealCache = new ConcurrentHashMap<>();
    private static ConstantChargesEntity constantCharges;

    // Meal caching methods as previously defined

    // Method to get constant charges from the cache
    public static ConstantChargesEntity getConstantCharges() {
        return constantCharges;
    }

    // Method to update constant charges in the cache
    public static void updateConstantCharges(ConstantChargesEntity charges) {
        constantCharges = charges;
    }
}

