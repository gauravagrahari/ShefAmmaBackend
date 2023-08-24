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
//    I will tell   you my requirement- so I am building an application which will help users book  three meals- breakfast , lunch and dinner, these meals would be delivered them within a time period, Now from before hand we will know the locaton of customer, the location of cooks which are spread at different locations of a region. the customer  needs to book the order before 1-2 hours. So this will give us a time window in which we will be able to plan the delivery. I will have 5-6 delivery boys. So this will allow us to plan the delivery ahead of time and hence the efficiency of delivery can be improved beforehand. This can save us time and money. Now I want you to create a method for me. WHich will take all the details. And assign the orders to apprpriate devBoys. Below I am going to give you all the entity classed--