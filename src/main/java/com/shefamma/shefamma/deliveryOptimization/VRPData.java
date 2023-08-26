package com.shefamma.shefamma.deliveryOptimization;

import com.shefamma.shefamma.entities.OrderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VRPData {
    private final DistanceMatrixService distanceMatrixService;
    private final Map<String, String> devBoyData;
    private final Map<String, List<OrderEntity>> guestGeocodeOrderMap;
    private final Map<String, List<OrderEntity>> hostGeocodeOrderMap;

    // Constructor
    public VRPData(DistanceMatrixService distanceMatrixService,
                   Map<String, String> devBoyData,
                   Map<String, List<OrderEntity>> guestGeocodeOrderMap,
                   Map<String, List<OrderEntity>> hostGeocodeOrderMap) {
        this.distanceMatrixService = distanceMatrixService;
        this.devBoyData = devBoyData;
        this.guestGeocodeOrderMap = guestGeocodeOrderMap;
        this.hostGeocodeOrderMap = hostGeocodeOrderMap;
    }

    // Create a distance matrix for all points (DevBoys, Hosts, and Guests)
    public int[][] createDistanceMatrix() {
        // Get all unique locations
        List<String> allLocations = new ArrayList<>(devBoyData.values());
        allLocations.addAll(guestGeocodeOrderMap.keySet());
        allLocations.addAll(hostGeocodeOrderMap.keySet());

        int size = allLocations.size();
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    matrix[i][j] = (int) distanceMatrixService.getDistanceBetween(allLocations.get(i), allLocations.get(j));
                } else {
                    matrix[i][j] = 0;
                }
            }
        }

        return matrix;
    }
}
