package com.shefamma.shefamma.deliveryOptimization;

import com.shefamma.shefamma.entities.OrderEntity;

import java.util.List;
import java.util.Map;

public class DeliveryOptimizationService {

    private DistanceMatrixService distanceMatrixService = new DistanceMatrixService();

    public void assignOrdersToDevBoys(Map<String, String> devBoyData,
                                      Map<String, List<OrderEntity>> guestGeocodeOrderMap,
                                      Map<String, List<OrderEntity>> hostGeocodeOrderMap) {

        // For each DevBoy
        for (Map.Entry<String, String> entry : devBoyData.entrySet()) {
            String devBoyId = entry.getKey();
            String devBoyGeocode = entry.getValue();

            // Find the closest order based on guest and host geocode
            OrderEntity closestOrder = findClosestOrder(devBoyGeocode, guestGeocodeOrderMap, hostGeocodeOrderMap);

            if (closestOrder != null) {
                // Assign order to DevBoy
                closestOrder.setUuidDevBoy(devBoyId);

                // Remove the order from the maps to avoid assigning it again
                guestGeocodeOrderMap.get(closestOrder.getGeoGuest()).remove(closestOrder);
                if (guestGeocodeOrderMap.get(closestOrder.getGeoGuest()).isEmpty()) {
                    guestGeocodeOrderMap.remove(closestOrder.getGeoGuest());
                }

                hostGeocodeOrderMap.get(closestOrder.getGeoHost()).remove(closestOrder);
                if (hostGeocodeOrderMap.get(closestOrder.getGeoHost()).isEmpty()) {
                    hostGeocodeOrderMap.remove(closestOrder.getGeoHost());
                }
            }
        }
    }

    private OrderEntity findClosestOrder(String devBoyGeocode,
                                         Map<String, List<OrderEntity>> guestGeocodeOrderMap,
                                         Map<String, List<OrderEntity>> hostGeocodeOrderMap) {
        OrderEntity closestOrder = null;
        double closestDistance = Double.MAX_VALUE;

        for (List<OrderEntity> orders : guestGeocodeOrderMap.values()) {
            for (OrderEntity order : orders) {
                double distanceToGuest = distanceMatrixService.getDistanceBetween(devBoyGeocode, order.getGeoGuest());
                double distanceToHost = distanceMatrixService.getDistanceBetween(devBoyGeocode, order.getGeoHost());
                double totalDistance = distanceToGuest + distanceToHost;

                if (totalDistance < closestDistance) {
                    closestDistance = totalDistance;
                    closestOrder = order;
                }
            }
        }
        return closestOrder;
    }
}
