package com.shefamma.shefamma.config;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {
    @Value("${google.maps.api-key}")
    private String apiKey;

    public GeocodingResult[] geocode(String address) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        return GeocodingApi.geocode(context, address).await();
    }
}