package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.deliveryOptimization.DeliveryOptimizationService;
import com.shefamma.shefamma.deliveryOptimization.DistanceMatrixService;
import com.shefamma.shefamma.deliveryOptimization.VRPData;
import com.shefamma.shefamma.deliveryOptimization.VRPSolver;
import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.shefamma.shefamma.HostRepository.RedisOrderImpl;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevBoyImpl implements DevBoy{
    @Autowired
    private RedisOrderImpl redisOrderImpl;

    @Autowired
    private DistanceMatrixService distanceMatrixService;

    private Map<String, String> devBoyData;//key is uuidDevBoy, value is geocode

    // Method to get orders and prepare the maps
//    public void assignOrdersToDevBoys(String mealType){
        public void assignOrdersToDevBoys(String mealType, List<OrderEntity> retrievedOrders){

//            List<OrderEntity> retrievedOrders = redisOrderImpl.getOrdersByMealType(mealType);

        // Create a map to store geocodes with their associated orders
        Map<String, List<OrderEntity>> guestGeocodeOrderMap = new HashMap<>();
        Map<String, List<OrderEntity>> hostGeocodeOrderMap = new HashMap<>();

        for (OrderEntity order : retrievedOrders) {
            guestGeocodeOrderMap.computeIfAbsent(order.getGeoGuest(), k -> new ArrayList<>()).add(order);
            hostGeocodeOrderMap.computeIfAbsent(order.getGeoHost(), k -> new ArrayList<>()).add(order);
        }


        // Call the VRP method for order assignment
        assignOrdersToDevBoysWithVRP(devBoyData, guestGeocodeOrderMap, hostGeocodeOrderMap);
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

        // Use the routes to assign orders to DevBoys
        for (int i = 0; i < routes.size(); i++) {
            List<Integer> route = routes.get(i);
            // Interpret this route to assign orders to the corresponding DevBoy.
            // Example:
            // String devBoyId = getDevBoyIdFromIndex(i);
            // assignOrdersToDevBoy(devBoyId, route, guestGeocodeOrderMap);
        }
    }
}
