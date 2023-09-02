package com.shefamma.shefamma.deliveryOptimization;

import com.shefamma.shefamma.entities.OrderEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VRPData {
    private final DistanceMatrixService distanceMatrixService;
    private final Map<String, String> devBoyData;
    private final Map<String, List<OrderEntity>> guestGeocodeOrderMap;
    private final Map<String, List<OrderEntity>> hostGeocodeOrderMap;
    private List<String> indexToLocationMapping;
    private Map<Integer, OrderEntity> indexToOrderMapping;
    // Constructor
    public VRPData(DistanceMatrixService distanceMatrixService,
                   Map<String, String> devBoyData,
                   Map<String, List<OrderEntity>> guestGeocodeOrderMap,
                   Map<String, List<OrderEntity>> hostGeocodeOrderMap) {
        this.distanceMatrixService = distanceMatrixService;
        this.devBoyData = devBoyData;
        this.guestGeocodeOrderMap = guestGeocodeOrderMap;
        this.hostGeocodeOrderMap = hostGeocodeOrderMap;
        this.indexToLocationMapping = new ArrayList<>();
        this.indexToOrderMapping = new HashMap<>();
    }

    public int[][] createDistanceMatrix() {
        // Reset mapping
        indexToLocationMapping.clear();
        indexToOrderMapping.clear();

        List<OrderEntity> allOrders = guestGeocodeOrderMap.values().stream().flatMap(List::stream).collect(Collectors.toList());

        for (OrderEntity order : allOrders) {
            indexToLocationMapping.add("PICKUP_" + order.getGeoHost());
            indexToLocationMapping.add("DELIVERY_" + order.getGeoGuest());

            indexToOrderMapping.put(indexToLocationMapping.size() - 2, order); // Mapping pickup node
            indexToOrderMapping.put(indexToLocationMapping.size() - 1, order); // Mapping delivery node
        }

        int size = indexToLocationMapping.size();
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    // Extract real geocode from the artificial nodes
                    String fromGeocode = indexToLocationMapping.get(i).contains("_") ? indexToLocationMapping.get(i).split("_")[1] : indexToLocationMapping.get(i);
                    String toGeocode = indexToLocationMapping.get(j).contains("_") ? indexToLocationMapping.get(j).split("_")[1] : indexToLocationMapping.get(j);
                    matrix[i][j] = (int) distanceMatrixService.getDistanceBetween(fromGeocode, toGeocode);
                } else {
                    matrix[i][j] = 0;
                }
            }
        }

        return matrix;
    }

    public OrderEntity getOrderFromIndex(int index) {
        return indexToOrderMapping.get(index);
    }
}
