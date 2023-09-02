package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.OrderEntity;

import java.util.List;
import java.util.Map;

public interface DevBoy {
void assignOrdersToDevBoys(String mealType);
//void assignOrdersToDevBoysWithVRP(Map<String, String> devBoyData, Map<String, List<OrderEntity>> guestGeocodeOrderMap, Map<String, List<OrderEntity>> hostGeocodeOrderMap);
}
