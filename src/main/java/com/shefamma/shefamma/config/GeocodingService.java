package com.shefamma.shefamma.config;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {
    private GeoApiContext context = new GeoApiContext.Builder()
            .apiKey("AIzaSyBgME4kzfk-uOisOH9Z1cUekeIAaf6z-FU")
            .build();

    public GeocodingResult[] geocode(String address) throws Exception {
        return GeocodingApi.geocode(context, address).await();
    }
}
