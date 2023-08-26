package com.shefamma.shefamma.deliveryOptimization;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DistanceMatrixService {

    private GeoApiContext context;

    @Value("${google.maps.api-key}")
    public void setApiKey(String apiKey) {
        this.context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    public double getDistanceBetween(String geocode1, String geocode2) {
        try {
            DistanceMatrix result = DistanceMatrixApi.newRequest(context)
                    .origins(geocode1)
                    .destinations(geocode2)
                    .mode(TravelMode.DRIVING) // You can change this to other modes like BICYCLING, WALKING, etc.
                    .await();

            if (result.rows.length > 0 && result.rows[0].elements.length > 0) {
                return result.rows[0].elements[0].distance.inMeters; // Returns distance in meters
            }
        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace();
        }
        return -1; // Indicate an error or unresolvable distance
    }
}
