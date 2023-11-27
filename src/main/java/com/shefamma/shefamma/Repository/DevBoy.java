package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.DevBoyEntity;

public interface DevBoy {
void assignOrdersToDevBoys(String mealType);

    DevBoyEntity update(String uuidDevBoy, String geocode, String attributeName, DevBoyEntity hostentity);
//void assignOrdersToDevBoysWithVRP(Map<String, String> devBoyData, Map<String, List<OrderEntity>> guestGeocodeOrderMap, Map<String, List<OrderEntity>> hostGeocodeOrderMap);
}
