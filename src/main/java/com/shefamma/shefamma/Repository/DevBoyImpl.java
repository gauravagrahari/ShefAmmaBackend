package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.shefamma.shefamma.deliveryOptimization.DistanceMatrixService;
import com.shefamma.shefamma.deliveryOptimization.VRPData;
import com.shefamma.shefamma.deliveryOptimization.VRPSolver;
import com.shefamma.shefamma.entities.DevBoyEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DevBoyImpl implements DevBoy{

    @Autowired
    private Order orderInterface;
    @Autowired
    private DistanceMatrixService distanceMatrixService;
    @Autowired
    private CommonMethods commonMethods;
    private Map<String, String> devBoyData;//key is uuidDevBoy, value is geocode

    // Method to get orders and prepare the maps
    public void assignOrdersToDevBoys(String mealType){
////        public void assignOrdersToDevBoys(String mealType, List<OrderEntity> retrievedOrders){
//
//            List<OrderEntity> retrievedOrders = redisOrderImpl.getOrdersByMealType(mealType);
//
//        // Create a map to store geocodes with their associated orders
//        Map<String, List<OrderEntity>> guestGeocodeOrderMap = new HashMap<>();
//        Map<String, List<OrderEntity>> hostGeocodeOrderMap = new HashMap<>();
//
//        for (OrderEntity order : retrievedOrders) {
//            guestGeocodeOrderMap.computeIfAbsent(order.getGeoGuest(), k -> new ArrayList<>()).add(order);
//            hostGeocodeOrderMap.computeIfAbsent(order.getGeoHost(), k -> new ArrayList<>()).add(order);
//        }
//
//
//        // Call the VRP method for order assignment
//        assignOrdersToDevBoysWithVRP(devBoyData, guestGeocodeOrderMap, hostGeocodeOrderMap);
    }

    // Method that utilizes VRP for order assignment
    private void assignOrdersToDevBoysWithVRP(Map<String, String> devBoyData, Map<String, List<OrderEntity>> guestGeocodeOrderMap, Map<String, List<OrderEntity>> hostGeocodeOrderMap) {
        VRPData vrpData = new VRPData(distanceMatrixService, devBoyData, guestGeocodeOrderMap, hostGeocodeOrderMap);
        int[][] distanceMatrix = vrpData.createDistanceMatrix();

        // Define the demands
        int[] demands = new int[distanceMatrix.length];
        Arrays.fill(demands, 1);  // Each guest has a demand of 1

        VRPSolver vrpSolver = new VRPSolver(distanceMatrix);
        List<List<Integer>> routes = vrpSolver.solve(devBoyData.size(), demands);

        for (int i = 0; i < routes.size(); i++) {
            List<Integer> route = routes.get(i);
            String devBoyId = getDevBoyIdFromIndex(i);

            for (int j = 0; j < route.size(); j++) {
                OrderEntity order = vrpData.getOrderFromIndex(route.get(j));
                if (j % 2 == 0) { // Pick-up
                    // Here, if required, you can register that the order was picked up
                } else { // Delivery
                    orderInterface.updateOrderDevBoyUuid(order.getUuidOrder(), order.getTimeStamp(), "uuidDevBoy", devBoyId);
                }
            }
        }}
    private String getDevBoyIdFromIndex(int index) {
        // Assuming devBoyData is a Map with keys as UUIDs and values as geocodes.
        // Convert the keySet (UUIDs) to a list and fetch by index.
        List<String> devBoyUUIDs = new ArrayList<>(devBoyData.keySet());
        return devBoyUUIDs.get(index);
    }
    private OrderEntity getOrderByIndex(int index, Map<String, List<OrderEntity>> guestGeocodeOrderMap) {
        // Convert the values (List<OrderEntity>) to a single list and fetch by index.
        List<OrderEntity> allOrders = guestGeocodeOrderMap.values().stream()
                .flatMap(List::stream)
                .toList();
        return allOrders.get(index);
    }

    public DevBoyEntity update(String partition, String sort, String attributeName, DevBoyEntity dev) {
        String value = null;
        // Get the value of the specified attribute
        switch (attributeName) {
            case "status":
                value = dev.getStatus();
                attributeName="stts";
                break;
            case "vehicleType":
                value = dev.getVehicleType();
                attributeName="veh";
                break;
            // Add more cases for other attributes if needed
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        // attributeName given to this method should be the attribute corresponding to the name in dynamodb table.
        UpdateItemResult response = commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
        System.out.println(response);

        try {
//            commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
            return dev;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Host entity. Error: " + e.getMessage());
        }
}}
